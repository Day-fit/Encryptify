package pl.dayfit.encryptifyauth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtSecretRotationService jwtSecretRotationService;
    private final EncryptifyUserCacheService encryptifyUserCacheService;

    /**
     * Generates JWT token based on parameters
     * @param username subject username
     * @param expiration token validity time (millis)
     * @param tokenType enum that represents token type
     * @return generated token
     */
    public String generateToken(String username, long expiration, JwtTokenType tokenType)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserByUsername(username);

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", user.getRoles());
        claims.put("tokenType", tokenType.toString());

        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()

            JWSSigner jwsSigner = new Ed25519Signer(jwtSecretRotationService.getCurrentOctetKeyPair());
            JWSObject jwsObject = new JWSObject
                    (
                            new JWSHeader.Builder(JWSAlgorithm.Ed25519)
                                    .type(JOSEObjectType.JWT)
                                    .keyID(
                                            String.valueOf(jwtSecretRotationService.getCurrentIndex())
                                    )
                                    .build(),
                            new Payload(claims)
                    );

            jwsObject.sign(jwsSigner);
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .signWith(jwtSecretRotationService.getCurrentPrivateKey())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuedAt(new Date())
                .claims(claims)
                .subject(user.getUsername())
                .header()
                .add("sk_id", jwtSecretRotationService.getCurrentIndex())
                .and()
                .compact();
    }
}
