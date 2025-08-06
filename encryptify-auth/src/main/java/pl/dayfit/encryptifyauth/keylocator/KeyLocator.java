package pl.dayfit.encryptifyauth.keylocator;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Locator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauth.service.JwtSecretRotationService;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class KeyLocator implements Locator<Key> {
    private final JwtSecretRotationService jwtSecretRotationService;

    @Override
    public Key locate(Header header) {
        Integer secretKeyId = (Integer) header.get("sk_id");

        if (secretKeyId == null)
        {
            throw new BadCredentialsException("Given JWT token is invalid");
        }

        return jwtSecretRotationService.getPublicKey(secretKeyId);
    }
}
