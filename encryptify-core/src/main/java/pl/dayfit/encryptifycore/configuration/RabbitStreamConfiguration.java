package pl.dayfit.encryptifycore.configuration;

import com.rabbitmq.stream.Address;
import com.rabbitmq.stream.Environment;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.rabbit.stream.producer.RabbitStreamTemplate;
import pl.dayfit.encryptifycore.configuration.properties.RabbitStreamConfigurationProperties;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({RabbitStreamConfigurationProperties.class})
public class RabbitStreamConfiguration {
    @Bean
    public Environment streamEnvironment(RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties)
    {
        final int streamPort = 5552;
        Address entrypoint = new Address("http://" + rabbitStreamConfigurationProperties.getHost(), streamPort);

        return Environment.builder()
                .rpcTimeout(Duration.ofSeconds(30))
                .username(rabbitStreamConfigurationProperties.getUsername())
                .password(rabbitStreamConfigurationProperties.getPassword())
                .host(entrypoint.host())
                .port(entrypoint.port())
                .addressResolver(address -> entrypoint)
                .locatorConnectionCount(1)
                .build();
    }

    @Bean
    public RabbitStreamTemplate streamTemplate(Environment streamEnvironment, RabbitStreamConfigurationProperties rabbitStreamConfigurationProperties)
    {
        RabbitStreamTemplate template = new RabbitStreamTemplate(streamEnvironment, rabbitStreamConfigurationProperties.getStatsStreamName());
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        return template;
    }
}
