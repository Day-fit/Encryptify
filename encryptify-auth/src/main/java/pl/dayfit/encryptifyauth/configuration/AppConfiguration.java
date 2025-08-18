package pl.dayfit.encryptifyauth.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CookieConfigurationProperties.class, EmailConfigurationProperties.class})
public class AppConfiguration {
}
