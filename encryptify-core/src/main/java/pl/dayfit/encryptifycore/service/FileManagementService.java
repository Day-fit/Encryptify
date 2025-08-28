package pl.dayfit.encryptifycore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.exception.FileActionException;
import pl.dayfit.encryptifycore.mapper.FileResponseMapper;
import pl.dayfit.encryptifycore.mapper.FileUploadDtoMapper;
import pl.dayfit.encryptifycore.cacheservice.DriveFileCacheService;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.helper.DriveFileAccessHelper;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {
    private final DriveFileAccessHelper accessHelper;
    private final DriveFileCacheService driveFileCacheService;
    private final FileUploadDtoMapper fileUploadDtoMapper;
    private final FileResponseMapper fileResponseMapper;

    /**
     * Handles process of uploading the file by saving it into filesystem and database
     * @param fileRequestDto dto with file representation
     * @param uploader username of the uploader
     * @return file's id
     */
    public long handleFileUpload(FileRequestDto fileRequestDto, String uploader)
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
                fos.write(fileRequestDto.base64Content()
                        .getBytes(StandardCharsets.UTF_8));
                fos.flush();
            }
        } catch (IOException ex) {
            log.warn("Failed to create file {}, reason: {}", driveFile.getPath(), ex.getMessage());
            throw new FileActionException("Failed to create file");
        }

        driveFileCacheService.save(driveFile);

        return driveFile.getId();
    }

    /**
     * Handles process of deleting a file
     * @param id id of file to delete
     * @param uploader file uploader username
     */
    public void handleFileDeletion(long id, String uploader) {
        if (!accessHelper.isOwner(id,  uploader))
        {
            throw new AccessDeniedException("You are not owner of this file");
        }

        try {
            DriveFile driveFile = driveFileCacheService.getDriveFileById(id);
            driveFileCacheService.deleteDriveFile(driveFile);
            Files.delete(Path.of(driveFile.getPath()));
        } catch (IOException ex) {
            throw new FileActionException("Failed to delete file");
        }
    }

    /**
     * Finds files in given folder
     * @param name uploader username (used if folderId is null)
     * @param folderId id of folder to search files in
     * @return DTO List of files in given folder
     */
    public List<FileResponseDto> getFiles(String name, @Nullable Long folderId) {
        if (folderId != null){
            return driveFileCacheService
                    .getFilesFromFolder(folderId)
                    .stream()
                    .map(fileResponseMapper::toDto)
                    .toList();
        }



        return driveFileCacheService
                .getParentlessFiles(name)
                .stream()
                .map(fileResponseMapper::toDto)
                .toList();
    }
}
