package pl.dayfit.encryptifycore.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifycore.configuration.FilesConfigurationProperties;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

import java.io.File;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(FilesConfigurationProperties.class)
public class FileUploadDtoMapperImpl implements FileUploadDtoMapper {
    private final FilesConfigurationProperties filesConfigurationProperties;

    @Override
    public DriveFile toDestination(FileRequestDto fileRequestDto, String uploader) {
        UUID uuid = UUID.randomUUID();
        DriveFile driveFile = new DriveFile();

        byte[] bytes = Base64.getDecoder().decode(fileRequestDto.base64Content());

        driveFile.setUuid(uuid);
        driveFile.setName(fileRequestDto.name());
        driveFile.setUploader(uploader);
        driveFile.setUploadDate(Instant.now());
        driveFile.setPath(filesConfigurationProperties.getSavePath() + File.separator + uuid);
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
