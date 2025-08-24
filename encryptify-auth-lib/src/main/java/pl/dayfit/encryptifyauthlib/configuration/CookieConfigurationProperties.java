package pl.dayfit.encryptifyauthlib.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cookies")
public class CookieConfigurationProperties {
    private String accessTokenName;
    private String refreshTokenName;
}
