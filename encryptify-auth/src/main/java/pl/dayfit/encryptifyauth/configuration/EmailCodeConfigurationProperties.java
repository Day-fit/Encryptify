package pl.dayfit.encryptifyauth.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "email.code")
public class EmailCodeConfigurationProperties {
    private long validityInMinutes;
}
