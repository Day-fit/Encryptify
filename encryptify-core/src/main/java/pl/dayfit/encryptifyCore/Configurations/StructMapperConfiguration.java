package pl.dayfit.encryptifyCore.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.dayfit.encryptifyCore.Mappers.FileUploadDtoMapper;
import pl.dayfit.encryptifyCore.Entities.EncryptifyFile;

@Configuration
public class StructMapperConfiguration {
    @Bean
    public FileUploadDtoMapper fileUploadDtoMapper()
    {
        return fileUploadDto -> new EncryptifyFile(

        );
    }
}
