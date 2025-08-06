package pl.dayfit.encryptifyauth.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cookies")
public class CookieConfigurationProperties {
    private boolean secured;
}
