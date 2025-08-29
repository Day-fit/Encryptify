package pl.dayfit.encryptifycore.service;

import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.event.UserReadyForSetupEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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

        if(alreadyExist)
        {
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

        minioClient.makeBucket(
                MakeBucketArgs.builder()
                        .bucket(username)
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
            throw new InsufficientDataException("File is too large to upload");
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
            throw new InsufficientDataException("File is too large to upload");
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
            throw new InsufficientDataException("File is too large to upload");
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
        );

        OutputStream base64Out = Base64.getEncoder().wrap(out)){
            response.transferTo(base64Out);
        } catch (IOException e) {
            throw new IOException("Error while deleting file");
        } catch (InsufficientDataException e) {
            throw new InsufficientDataException("File is too large to upload");
        } catch (Exception e) {
            throw new IllegalStateException("Internal error while deleting file");
        }
    }
}
