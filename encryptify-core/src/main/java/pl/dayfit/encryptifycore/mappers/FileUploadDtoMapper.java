package pl.dayfit.encryptifycore.mappers;

import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifydata.entity.DriveFile;

public interface FileUploadDtoMapper {
    DriveFile toDestination(FileRequestDto fileRequestDto, String uploadedBy);
    FileResponseDto toResponseDto(DriveFile driveFile);
}
