package pl.dayfit.encryptifycore.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "files")
public class FilesConfigurationProperties {
    @Setter
    @Getter
    private String savePath;
}
