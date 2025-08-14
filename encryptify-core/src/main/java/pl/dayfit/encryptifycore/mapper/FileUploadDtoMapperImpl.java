package pl.dayfit.encryptifycore.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import pl.dayfit.encryptifycore.configuration.FilesConfigurationProperties;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

import java.io.File;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadDtoMapperImpl implements FileUploadDtoMapper {
    private final FilesConfigurationProperties filesConfigurationProperties;
    private String savePath;

    @PostMapping
    private void init()
    {
        savePath = filesConfigurationProperties.getSavePath();
    }

    @Override
    public DriveFile toDestination(FileRequestDto fileRequestDto, String uploader) {
        UUID uuid = UUID.randomUUID();
        DriveFile driveFile = new DriveFile();

        driveFile.setUuid(uuid);
        driveFile.setName(fileRequestDto.name());
        driveFile.setContent(Base64.getDecoder().decode(fileRequestDto.base64Content()));
        driveFile.setUploader(uploader);
        driveFile.setUploadDate(Instant.now());

        driveFile.setPath(savePath + File.separator + uuid);

        return driveFile;
    }
}
