package pl.dayfit.encryptifyCore.Mappers;

import org.mapstruct.Mapper;
import pl.dayfit.encryptifyCore.Dto.FileUploadDto;
import pl.dayfit.encryptifyCore.Entities.EncryptifyFile;

@Mapper
public interface FileUploadDtoMapper {
    EncryptifyFile toDestination(FileUploadDto fileUploadDto);
}
