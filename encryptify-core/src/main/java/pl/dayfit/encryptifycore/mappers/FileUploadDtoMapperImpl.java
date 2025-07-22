package pl.dayfit.encryptifycore.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifydata.cacheservices.UserCacheService;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifydata.entities.DriveFile;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUploadDtoMapperImpl implements FileUploadDtoMapper {
    private final UserCacheService cacheService;

    @Override
    public DriveFile toDestination(FileRequestDto fileRequestDto, String uploadedBy) {
        DriveFile driveFile = new DriveFile();

        driveFile.setUuid(UUID.randomUUID());
        driveFile.setName(fileRequestDto.name());
        driveFile.setContent(Base64.getDecoder().decode(fileRequestDto.base64Content()));
        driveFile.setUploadedBy(cacheService.getUserByUsername(uploadedBy));
        driveFile.setUploadDate(Instant.now());

        return driveFile;
    }

    @Override
    public FileResponseDto toResponseDto(DriveFile driveFile) {
        return new FileResponseDto(
                driveFile.getName(),
                driveFile.getUploadedBy().getUsername(),
                Base64.getEncoder().encodeToString(driveFile.getContent()),
                driveFile.getUploadDate()
        );
    }
}
