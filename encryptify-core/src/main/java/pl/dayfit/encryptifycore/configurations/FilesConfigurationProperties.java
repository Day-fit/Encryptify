package pl.dayfit.encryptifycore.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "files")
public class FilesConfigurationProperties {
    @Setter
    @Getter
    private String savePath;
}
