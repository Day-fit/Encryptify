package pl.dayfit.encryptifyauthlib.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("jwt")
public class JwtConfigurationProperties {
    private String accessTokenName;
    private String refreshTokenName;

    private int accessTokenValidityMinutes;
    private int refreshTokenValidityDays;
}
