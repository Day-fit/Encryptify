package pl.dayfit.encryptifycore.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rabbitmq.streams")
public class RabbitStreamConfigurationProperties {
    private String host;
    private String username;
    private String password;

    private String statsStreamName;
}
