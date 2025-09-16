package pl.dayfit.encryptifycore.mapper;

import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

import java.util.UUID;

public interface FileUploadDtoMapper {
    DriveFile toDestination(FileRequestDto fileRequestDto, long fileSize, UUID uploaderId);
}
