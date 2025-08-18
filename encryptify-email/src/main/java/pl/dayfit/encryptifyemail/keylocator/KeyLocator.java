package pl.dayfit.encryptifyemail.keylocator;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Locator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.dayfit.encryptifyemail.service.JwtRotationListener;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class KeyLocator implements Locator<Key> {
    private final JwtRotationListener jwtRotationListener;

    @Override
    public Key locate(Header header) {
        Integer secretKeyId = (Integer) header.get("sk_id");

        if (secretKeyId == null)
        {
            throw new IllegalArgumentException("Verification token is invalid");
        }

        Key key = jwtRotationListener.getSecretKey(secretKeyId);

        if (key == null)
        {
            throw new IllegalArgumentException("Verification token is invalid");
        }

        return key;
    }
}
