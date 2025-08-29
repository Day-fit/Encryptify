package pl.dayfit.encryptifycore.mapper;

import org.mapstruct.Mapper;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

@Mapper(componentModel = "spring")
public interface FileResponseMapper {
    FileResponseDto toDto(DriveFile file);
}
