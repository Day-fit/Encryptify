package pl.dayfit.encryptifycore.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "pl.dayfit.encryptifycore.repository")
@EntityScan(
        basePackages = "pl.dayfit.encryptifycore.entity"
)
public class JpaConfiguration {
}
