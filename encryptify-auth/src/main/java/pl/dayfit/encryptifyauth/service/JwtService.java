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

        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .expirationTime(new Date(System.currentTimeMillis() + expiration))
                    .issueTime(new Date())
                    .jwtID(UUID.randomUUID().toString())
                    .claim("roles", user.getRoles())
                    .claim("tokenType", tokenType.toString())
                    .build();

            JWSSigner jwsSigner = new Ed25519Signer(jwtSecretRotationService.getCurrentOctetKeyPair());
            JWSObject jwsObject = new JWSObject
                    (
                            new JWSHeader.Builder(JWSAlgorithm.Ed25519)
                                    .type(JOSEObjectType.JWT)
                                    .keyID(
                                            String.valueOf(jwtSecretRotationService.getCurrentIndex())
                                    )
                                    .build(),

                            new Payload(claimsSet.getClaims())
                    );

            jwsObject.sign(jwsSigner);

            return jwsObject.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
