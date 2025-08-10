package pl.dayfit.encryptifyauth.helper;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class HashHelper {
    /**
     * Generates lookup to make logging with email possible
     * @param rawEmail Raw form of email (not hashed, encrypted etc.)
     * @return An 40 characters long lookup
     */
    public String generateEmailLookup(String rawEmail)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            return Base64.getEncoder().encodeToString(md.digest(rawEmail.getBytes(StandardCharsets.UTF_8))).substring(0, 39);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
