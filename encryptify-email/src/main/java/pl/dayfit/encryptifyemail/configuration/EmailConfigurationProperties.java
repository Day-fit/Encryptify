package pl.dayfit.encryptifyemail.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "email")
public class EmailConfigurationProperties {
    private String from;
    private String subject;
}
