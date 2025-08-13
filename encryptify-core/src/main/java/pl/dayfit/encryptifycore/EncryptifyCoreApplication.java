package pl.dayfit.encryptifycore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages =
        {
                "pl.dayfit.encryptifycore",
                "pl.dayfit.encryptifyauthlib"
        })
public class EncryptifyCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptifyCoreApplication.class, args);
    }
}
