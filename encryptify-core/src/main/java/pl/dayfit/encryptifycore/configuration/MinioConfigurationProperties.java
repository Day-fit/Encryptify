package pl.dayfit.encryptifycore.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "minio")
public class MinioConfigurationProperties {
    private String url;
    private String username;
    private String password;
}
