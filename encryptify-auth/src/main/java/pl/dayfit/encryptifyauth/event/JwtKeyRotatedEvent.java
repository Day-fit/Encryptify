package pl.dayfit.encryptifyauth.event;

import com.nimbusds.jose.jwk.OctetKeyPair;

public record JwtKeyRotatedEvent(OctetKeyPair key, int keyId) {
}
