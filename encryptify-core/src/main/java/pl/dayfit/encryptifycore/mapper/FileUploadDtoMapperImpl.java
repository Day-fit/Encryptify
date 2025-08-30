package pl.dayfit.encryptifycore.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifycore.cacheservice.DriveFolderCacheService;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.entity.DriveFile;
import pl.dayfit.encryptifycore.entity.DriveFolder;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadDtoMapperImpl implements FileUploadDtoMapper {
    private final DriveFolderCacheService driveFolderCacheService;

    @Override
    public DriveFile toDestination(FileRequestDto fileRequestDto, String uploader) {
        DriveFolder driveFolder = null;

        if (fileRequestDto.folderId() != null)
        {
            driveFolder = driveFolderCacheService.getDriveDirectoryById(fileRequestDto.folderId());
        }

        final String name = fileRequestDto.name();
        DriveFile driveFile = new DriveFile();

        byte[] bytes = Base64.getDecoder().decode(fileRequestDto.base64Content()); //Decoding to save 33% of space

        String path = "";

        if (driveFolder != null)
        {
            path = driveFolder.getPath();
            driveFile.setParent(driveFolder);
        }

        driveFile.setUuid(UUID.randomUUID());
        driveFile.setName(name);
        driveFile.setUploader(uploader);
        driveFile.setUploadDate(Instant.now());
        driveFile.setPath(path + name);
        driveFile.setFileSize(
                formatBytes(bytes.length)
        );

        return driveFile;
    }

    private static String formatBytes(long bytes) {
        double value = bytes;
        String unit;

        if (bytes < 1024) {
            unit = "B";
        } else if (bytes < 1024L * 1024) {
            value = bytes / 1024.0;
            unit = "KB";
        } else if (bytes < 1024L * 1024 * 1024) {
            value = bytes / (1024.0 * 1024);
            unit = "MB";
        } else if (bytes < 1024L * 1024 * 1024 * 1024) {
            value = bytes / (1024.0 * 1024 * 1024);
            unit = "GB";
        } else {
            value = bytes / (1024.0 * 1024 * 1024 * 1024);
            unit = "TB";
        }

        return String.format("%.2f %s", value, unit);
    }
}
