package pl.dayfit.encryptifyauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages =
        {
                "pl.dayfit.encryptifyauth",
                "pl.dayfit.encryptifyauthlib"
        })
public class EncryptifyAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptifyAuthApplication.class, args);
    }

}
