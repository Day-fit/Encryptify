package pl.dayfit.encryptifycore.mapper;

import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

public interface FileUploadDtoMapper {
    DriveFile toDestination(FileRequestDto fileRequestDto, String uploader);
    FileResponseDto toResponseDto(DriveFile driveFile);
}
