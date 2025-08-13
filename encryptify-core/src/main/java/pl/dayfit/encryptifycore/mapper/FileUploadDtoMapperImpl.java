package pl.dayfit.encryptifycore.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifycore.configuration.FilesConfigurationProperties;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadDtoMapperImpl implements FileUploadDtoMapper {
    private final FilesConfigurationProperties filesConfigurationProperties;

    @Override
    public DriveFile toDestination(FileRequestDto fileRequestDto, String uploader) {
        DriveFile driveFile = new DriveFile();

        driveFile.setUuid(UUID.randomUUID());
        driveFile.setName(fileRequestDto.name());
        driveFile.setContent(Base64.getDecoder().decode(fileRequestDto.base64Content()));
        driveFile.setUploader(uploader);
        driveFile.setUploadDate(Instant.now());

        return driveFile;
    }

    @Override
    public FileResponseDto toResponseDto(DriveFile driveFile) {
        return new FileResponseDto(
                driveFile.getName(),
                driveFile.getUploader(),
                Base64.getEncoder().encodeToString(driveFile.getContent()),
                driveFile.getUploadDate()
        );
    }
}
