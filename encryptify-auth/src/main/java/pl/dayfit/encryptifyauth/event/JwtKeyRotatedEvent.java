package pl.dayfit.encryptifyauth.event;

import java.security.Key;

public record JwtKeyRotatedEvent(Key key, int keyId) {
}
