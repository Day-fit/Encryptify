package pl.dayfit.encryptifycore.configuration;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioConfigurationProperties.class)
@RequiredArgsConstructor
public class MinioConfiguration {
    private final MinioConfigurationProperties minioConfigurationProperties;

    @Bean
    public MinioClient minioClient()
    {
        return MinioClient.builder()
                .endpoint(minioConfigurationProperties.getUrl())
                .credentials(minioConfigurationProperties.getUsername(), minioConfigurationProperties.getPassword())
                .build();
    }
}
