package pl.dayfit.encryptifycore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.entity.DriveFile;

@Mapper(componentModel = "spring")
public interface FileResponseMapper {

    @Mapping(source = "parent.name", target = "parent")
    FileResponseDto toDto(DriveFile file);
}
