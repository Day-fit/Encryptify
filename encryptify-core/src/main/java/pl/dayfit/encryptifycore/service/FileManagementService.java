package pl.dayfit.encryptifycore.service;

import io.minio.errors.InsufficientDataException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.dayfit.encryptifycore.dto.ActivityStreamDto;
import pl.dayfit.encryptifycore.dto.FileRenameDto;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.exception.FileActionException;
import pl.dayfit.encryptifycore.mapper.FileUploadDtoMapper;
import pl.dayfit.encryptifycore.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.helper.DriveFileAccessHelper;
import pl.dayfit.encryptifycore.type.ActivityType;
import pl.dayfit.encryptifycore.type.TargetType;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;
    private final MinioService minioService;
    private final StatisticsCommunicationService statisticsCommunicationService;

    /**
     * Handles process of uploading the file by saving it into filesystem and database
     * @param fileRequestDto dto with file representation
     * @param file multipart file that will be uploaded
     * @param userId UUID of the userId
     * @return file's id
     */
    @Transactional
    public long handleFileUpload(FileRequestDto fileRequestDto, final MultipartFile file, String userId, String bucketName)
    {
        DriveFile driveFile = fileUploadDtoMapper.toDestination(
                fileRequestDto,
                file.getSize(),
                UUID.fromString(userId)
        );

        String path = driveFile.getPath();

        try {
            minioService.addFile(file.getInputStream(), path, bucketName);
        } catch (IOException ex) {
            log.warn("Failed to create file {}, reason: {}", path, ex.getMessage());
            throw new FileActionException("Failed to create file");
        } catch (InsufficientDataException ex) {
            log.warn("Insufficient data for file {}", path);
            throw new FileActionException("Insufficient data for file");
        }

        driveFileCacheService.save(driveFile);
        return driveFile.getId();
    }

    /**
     * Handles process of deleting a file
     * @param id id of file to delete
     * @param uploaderId file uploader UUID
     * @param bucketName bucket to delete file inside
     */
    @Transactional
    public void handleFileDeletion(long id, UUID uploaderId, String bucketName) {
        if (!accessHelper.isOwner(id, uploaderId))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        DriveFile driveFile = driveFileCacheService.getDriveFileById(id);
        driveFileCacheService.deleteDriveFile(driveFile);

        try {
            minioService.deleteFile(driveFile.getPath(), bucketName);
        } catch (IOException ex) {
            throw new FileActionException("Failed to delete file");
        } catch (InsufficientDataException ex) {
            throw new FileActionException("Insufficient data for file");
        }

        statisticsCommunicationService.sendActivity(
                new ActivityStreamDto(
                        uploaderId,
                        driveFile.getName(),
                        driveFile.getFileSize(),
                        ActivityType.DELETION,
                        TargetType.FILE,
                        Instant.now()
                )
        );
    }

    /**
     * Handles process of downloading the file by streaming Base64 into given OutputStream
     * @param fileId id of file to download
     * @param username username of download issuer (TEMPORAL: it will be replaced when file sharing will be introduced)
     * @param bucketName bucket to download file inside
     * @param out Servlet OutputStream
     */
    public void handleFileDownload(long fileId, String username, String bucketName, OutputStream out) {
        DriveFile driveFile = driveFileCacheService.getDriveFileById(fileId);

        if (!accessHelper.isOwner(driveFile, username))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }
        String path = driveFile.getPath();

        try {
            minioService
                    .downloadFile(path, bucketName, out);
        } catch (IOException exception) {
            log.warn("Failed to download file {}, reason: {}", path, exception.getMessage());
            throw new FileActionException("Failed to download file");
        } catch (InsufficientDataException ex) {
            log.warn("Failed to download file, insufficient data for file {}", path);
            throw new FileActionException("Insufficient data for file");
        }
    }

    public void handleFileRenaming(FileRenameDto dto, String username, String bucketName) {
        DriveFile driveFile = driveFileCacheService.getDriveFileById(dto.id());

        String path = driveFile.getPath();
        String newName = dto.newName();
        String[] fragments = path.split("/");
        fragments[fragments.length - 1] = newName;

        String newPath = String.join("/", fragments);

        if (!accessHelper.isOwner(driveFile, username))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        try {
            minioService
                    .renameFile(path, newName, bucketName);
        } catch (IOException exception) {
            log.warn("Failed to rename file {}, reason: {}", path, exception.getMessage());
            throw new FileActionException("Failed to rename file");
        } catch (InsufficientDataException ex) {
            log.warn("Failed to rename file, insufficient data for file {}", path);
            throw new FileActionException("Insufficient data for file");
        }

        driveFile.setPath(newPath);
        driveFile.setName(newName);
        driveFileCacheService.save(driveFile);
    }
}
