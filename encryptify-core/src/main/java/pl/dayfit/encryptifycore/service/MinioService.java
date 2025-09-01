package pl.dayfit.encryptifycore.service;

import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.entity.DriveFolder;
import pl.dayfit.encryptifycore.event.UserReadyForSetupEvent;
import pl.dayfit.encryptifycore.exception.FileActionException;

import java.io.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @RabbitListener(queues = "minio.configurer")
    public void setupBucket(UserReadyForSetupEvent event) throws Exception
    {
        String username = event.username();

        boolean alreadyExist = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(username)
                        .build()
        );

        if(!alreadyExist)
        {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(username)
                            .build()
            );

            return;
        }

        Iterable<Result<Item>> results =  minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(username)
                        .recursive(true)
                        .build()
        );

        List<DeleteObject> toDelete = new ArrayList<>();

        for (Result<Item> result : results)
        {
            Item item = result.get();
            toDelete.add(new DeleteObject(item.objectName()));
        }

        minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(username)
                        .objects(toDelete)
                        .build()
        );
    }

    public void addFile(String base64Content, DriveFile driveFile) throws IOException, InsufficientDataException {
        try(InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(base64Content)))
        {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(driveFile.getUploader())
                            .object(driveFile.getPath())
                            .stream(stream, stream.available(), -1)
                    .build()
            );
        } catch (IOException e) {
            throw new IOException("Error while uploading file");
        } catch (InsufficientDataException e) {
            throw new InsufficientDataException("Insufficient data for file upload");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteFile(String path, String bucket) throws IOException, InsufficientDataException {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
        } catch (IOException e) {
            throw new IOException("Error while deleting file");
        } catch (InsufficientDataException e) {
            throw new InsufficientDataException("Insufficient data for file deletion");
        } catch (Exception e) {
            throw new IllegalStateException("Internal error while deleting file");
        }
    }

    public void deleteFiles(String bucket, String... paths) throws IOException, InsufficientDataException {
        try {
            Iterable<DeleteObject> toDelete = Arrays.stream(paths).map(DeleteObject::new).toList();

            Iterable<Result<DeleteError>> errors = minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucket)
                            .objects(toDelete)
                            .build()
            );

            for (Result<DeleteError> result : errors) {
                log.warn("Error while deleting folder. Reason: {}", result.get().message());
            }
        } catch (IOException e) {
            throw new IOException("Error while deleting file");
        } catch (InsufficientDataException e) {
            throw new InsufficientDataException("Insufficient data for file deletion");
        } catch (Exception e) {
            throw new IllegalStateException("Internal error while deleting file");
        }
    }

    public void downloadFile(String path, String bucket, OutputStream out) throws IOException, InsufficientDataException {
        try(GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build()
        )){
            response.transferTo(out);
        } catch (IOException e) {
            throw new IOException("Error while downloading file");
        } catch (InsufficientDataException e) {
            throw new InsufficientDataException("Insufficient data for file download");
        } catch (Exception e) {
            throw new IllegalStateException("Internal error while downloading file");
        }
    }

    /**
     * Handles process of renaming folder. Rollback will be introduced later
     * @param newBasePath name of the new base path
     * @param oldBasePath name of the old base path
     * @param folder instance of the DriveFolder Entity
     * @throws FileActionException when exception is thrown during execution
     */
    public void renameFolder(String newBasePath, String oldBasePath, DriveFolder folder) {
        String uploader = folder.getUploader();
        List<Pair<String, String>> pathPairs = getPathsToChange(folder, newBasePath, oldBasePath);
        List<String> toDelete = new ArrayList<>();

        try {
            for (Pair<String, String> pair : pathPairs)
            {
                    String oldPath = pair.getLeft();
                    minioClient.copyObject(
                            CopyObjectArgs.builder()
                                    .bucket(folder.getUploader())
                                    .object(pair.getRight())
                                    .source(
                                            CopySource.builder()
                                                    .bucket(uploader)
                                                    .object(oldPath)
                                                    .build()
                                    )
                                    .build()
                    );

                    toDelete.add(oldPath);
            }

            deleteFiles(uploader, toDelete.toArray(String[]::new));
        } catch (Exception e) {
            log.warn("Error while renaming folder. Reason: {}", e.getMessage());
            throw new FileActionException("Error while renaming folder. Try again later.");
        }
    }

    /**
     * Scans folder and subfolders for files, calculates new files paths
     * @param rootFolder instance of DriveFolder that represents root folder
     * @param newBasePath String representing new base path for folder files
     * @param oldBasePath String representing old base path of folder
     * @return Pair of paths where left is old path, and right is new path
     */
    private List<Pair<String, String>> getPathsToChange(DriveFolder rootFolder, String newBasePath, String oldBasePath)
    {
        List<Pair<String, String>> result = new ArrayList<>();
        List<DriveFolder> folders = new ArrayList<>();
        folders.add(rootFolder);

        while (!folders.isEmpty())
        {
            DriveFolder folder = folders.remove(0);
            folders.addAll(folder.getChildren());

            result.addAll(
                    folder.getFiles()
                            .stream()
                            .map(file ->
                            {
                                String oldPath = file.getPath();
                                return Pair.of(oldPath, newBasePath + oldPath.substring(oldBasePath.length()));
                            }).toList()
            );
        }

        return result;
    }
}
