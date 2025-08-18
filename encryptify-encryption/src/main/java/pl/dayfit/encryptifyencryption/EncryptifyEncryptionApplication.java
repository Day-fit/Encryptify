package pl.dayfit.encryptifyencryption;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "pl.dayfit.encryptifyencryption",
                "pl.dayfit.encryptifyauthlib"
        }
)
public class EncryptifyEncryptionApplication {
    public static void main(String[] args) {
        SpringApplication.run(EncryptifyEncryptionApplication.class, args);
    }
}
