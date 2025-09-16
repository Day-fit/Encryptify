package pl.dayfit.encryptifycore.configuration;

import com.rabbitmq.stream.Environment;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import pl.dayfit.encryptifycore.configuration.properties.RabbitStreamConfigurationProperties;

@Configuration
@EnableConfigurationProperties({RabbitStreamConfigurationProperties.class})
public class RabbitStreamConfiguration {
    @Bean
    public Environment streamEnvironment(RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties)
    {
        return Environment.builder()
                .username(rabbitStreamConfigurationProperties.getUsername())
                .password(rabbitStreamConfigurationProperties.getPassword())
                .host(rabbitStreamConfigurationProperties.getHost())
                .build();
    }

    @Bean
    public RabbitStreamTemplate streamTemplate(Environment streamEnvironment, RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties)
    {
        return new RabbitStreamTemplate(streamEnvironment, rabbitStreamConfigurationProperties.getStatsStreamName());
    }
}
