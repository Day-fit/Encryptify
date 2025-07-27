package pl.dayfit.encryptifydata.configurations;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "pl.dayfit.encryptifydata.repositories")
@EntityScan(
        basePackages = "pl.dayfit.encryptifydata.entities"
)
public class JpaConfiguration {
}
