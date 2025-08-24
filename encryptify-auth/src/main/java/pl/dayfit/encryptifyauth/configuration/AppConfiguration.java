package pl.dayfit.encryptifyauth.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.dayfit.encryptifyauthlib.configuration.CookieConfigurationProperties;

@Configuration
@EnableConfigurationProperties({CookieConfigurationProperties.class})
public class AppConfiguration {
}
