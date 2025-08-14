package pl.dayfit.encryptifyauthlib.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "security")
public class SecurityConfigurationProperties {
    private List<String> securedEndpoints;
    private List<String> allowedOriginsPatterns;
}
