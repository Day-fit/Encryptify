package pl.dayfit.encryptifycore.service;

import io.minio.errors.InsufficientDataException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.exception.FileActionException;
import pl.dayfit.encryptifycore.mapper.FileUploadDtoMapper;
import pl.dayfit.encryptifycore.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.helper.DriveFileAccessHelper;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;
    private final MinioService minioService;

    /**
     * Handles process of uploading the file by saving it into filesystem and database
     * @param fileRequestDto dto with file representation
     * @param uploader username of the uploader
     * @return file's id
     */
    @Transactional
    public long handleFileUpload(FileRequestDto fileRequestDto, String uploader)
    {
        DriveFile driveFile = fileUploadDtoMapper.toDestination(
                fileRequestDto,
                uploader
        );

        try {
            minioService.addFile(fileRequestDto.base64Content(), driveFile);
        } catch (IOException ex) {
            log.warn("Failed to create file {}, reason: {}", driveFile.getPath(), ex.getMessage());
            throw new FileActionException("Failed to create file");
        } catch (InsufficientDataException ex) {
            log.warn("Insufficient data for file {}", driveFile.getPath());
            throw new FileActionException("Insufficient data for file");
        }

        driveFileCacheService.save(driveFile);
        return driveFile.getId();
    }

    /**
     * Handles process of deleting a file
     * @param id id of file to delete
     * @param uploader file uploader username
     */
    @Transactional
    public void handleFileDeletion(long id, String uploader) {
        if (!accessHelper.isOwner(id, uploader))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        try {
            DriveFile driveFile = driveFileCacheService.getDriveFileById(id);
            driveFileCacheService.deleteDriveFile(driveFile);

            minioService.deleteFile(driveFile.getPath(), uploader);
        } catch (IOException ex) {
            throw new FileActionException("Failed to delete file");
        } catch (InsufficientDataException ex) {
            throw new FileActionException("Insufficient data for file");
        }
    }

    /**
     * Handles process of downloading the file by streaming Base64 into given OutputStream
     * @param fileId id of file to download
     * @param username username of download issuer (TEMPORAL: it will be replaced when file sharing will be introduced)
     * @param out Servlet OutputStream
     */
    public void handleFileDownload(long fileId, String username, OutputStream out) {
        DriveFile driveFile = driveFileCacheService.getDriveFileById(fileId);

        if (!accessHelper.isOwner(driveFile, username))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        try {
            minioService
                    .downloadFile(driveFile.getPath(), username, out);
        } catch (IOException exception) {
            log.warn("Failed to download file {}, reason: {}", driveFile.getPath(), exception.getMessage());
            throw new FileActionException("Failed to download file");
        } catch (InsufficientDataException ex) {
            log.warn("Failed to download file, insufficient data for file {}", driveFile.getPath());
            throw new FileActionException("Insufficient data for file");
        }
    }
}
