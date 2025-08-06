package pl.dayfit.encryptifycore.configurations;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FilesConfigurationProperties.class)
public class AppConfiguration {
}
