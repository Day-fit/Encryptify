package pl.dayfit.encryptify.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "pl.dayfit.encryptify.stats",
                "pl.dayfit.encryptifyauthlib"
        }
)
public class EncryptifyStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptifyStatisticsApplication.class, args);
    }
}
