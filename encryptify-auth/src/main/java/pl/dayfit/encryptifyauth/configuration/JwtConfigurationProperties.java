package pl.dayfit.encryptifyauth.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigurationProperties {
    private int accessTokenValidityMinutes;
    private int refreshTokenValidityDays;
    private boolean useSecureCookies = true;
}
