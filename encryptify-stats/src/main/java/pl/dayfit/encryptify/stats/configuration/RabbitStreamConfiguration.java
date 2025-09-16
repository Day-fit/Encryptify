package pl.dayfit.encryptify.stats.configuration;

import com.rabbitmq.stream.Environment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.dayfit.encryptify.stats.configuration.properties.RabbitStreamConfigurationProperties;

@Configuration
@EnableConfigurationProperties({RabbitStreamConfigurationProperties.class})
public class RabbitStreamConfiguration {
    @Bean
    public Environment environment(RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties)
    {
        return Environment.builder()
                .username(rabbitStreamConfigurationProperties.getUsername())
                .password(rabbitStreamConfigurationProperties.getPassword())
                .host(rabbitStreamConfigurationProperties.getHost())
                .build();
    }
}
