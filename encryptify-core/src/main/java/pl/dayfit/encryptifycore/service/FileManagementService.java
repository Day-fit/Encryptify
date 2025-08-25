package pl.dayfit.encryptifycore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.exception.FailedToCreateFileException;
import pl.dayfit.encryptifycore.mapper.FileUploadDtoMapper;
import pl.dayfit.encryptifycore.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.helper.DriveFileAccessHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;

    public void handleFileUpload(FileRequestDto fileRequestDto, String uploader)
    {
        DriveFile driveFile = fileUploadDtoMapper.toDestination(
                fileRequestDto,
                uploader
        );

        try {
            Path path = Path.of(driveFile.getPath());

            Files.createFile(path);

            try (FileOutputStream fos = new FileOutputStream(path.toFile()))
            {
                fos.write(driveFile.getContent());
                fos.flush();
            }
        } catch (IOException ex) {
            log.warn("Failed to create file {}, reason: {}", driveFile.getPath(), ex.getMessage());
            throw new FailedToCreateFileException(ex.getMessage());
        }

        driveFileCacheService.save(driveFile);
    }

    public void handleFileDeletion(Long id, String uploader) {
        if (!accessHelper.isOwner(id,  uploader))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        driveFileCacheService.deleteDriveFile(id);
    }
}
