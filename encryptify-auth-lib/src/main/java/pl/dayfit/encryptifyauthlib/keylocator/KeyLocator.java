package pl.dayfit.encryptifyauthlib.keylocator;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Locator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyauthlib.service.JwtRotationListener;

import java.security.Key;

@Component
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
public class KeyLocator implements Locator<Key> {
    private final JwtRotationListener jwtRotationListener;

    @Override
    public Key locate(Header header) {
        Integer secretKeyId = (Integer) header.get("sk_id");

        if (secretKeyId == null)
        {
            throw new BadCredentialsException("Given JWT token is invalid");
        }

        return jwtRotationListener.getSecretKey(secretKeyId);
    }
}
