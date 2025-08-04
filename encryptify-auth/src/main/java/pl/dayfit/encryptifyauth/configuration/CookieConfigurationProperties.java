package pl.dayfit.encryptifyauth.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "cookies")
public class CookieConfigurationProperties {
    private String refreshTokenName;
    private String accessTokenName;
    private boolean secured;
    private int refreshTokenValidityDays;
    private int accessTokenValidityMinutes;
}
