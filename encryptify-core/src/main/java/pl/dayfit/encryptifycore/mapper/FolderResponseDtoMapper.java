package pl.dayfit.encryptifycore.mapper;

import org.mapstruct.Mapper;
import pl.dayfit.encryptifycore.dto.FolderResponseDto;
import pl.dayfit.encryptifycore.entity.DriveFolder;

@Mapper(componentModel = "spring")
public interface FolderResponseDtoMapper {
    FolderResponseDto toDto(DriveFolder folder);
}
