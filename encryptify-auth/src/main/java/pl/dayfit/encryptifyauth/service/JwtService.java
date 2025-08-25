package pl.dayfit.encryptifyauth.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;
import pl.dayfit.encryptifyauth.entity.EncryptifyUser;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtSecretRotationService jwtSecretRotationService;
    private final EncryptifyUserCacheService encryptifyUserCacheService;

    /**
     * Generates JWT token based on parameters
     * @param username subject username
     * @param expiration token validity time
     * @param timeUnit time unit of the expiration time
     * @param tokenType enum that represents token type
     * @return generated token
     */
    public String generateToken(String username, long expiration, TimeUnit timeUnit, JwtTokenType tokenType)
    {
        expiration = timeUnit.toMillis(expiration);
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
            SignedJWT jwsObject = new SignedJWT
                    (
                            new JWSHeader.Builder(JWSAlgorithm.Ed25519)
                                    .type(JOSEObjectType.JWT)
                                    .keyID(
                                            String.valueOf(jwtSecretRotationService.getCurrentIndex())
                                    )
                                    .build(),

                            claimsSet
                    );

            jwsObject.sign(jwsSigner);

            return jwsObject.serialize();
        } catch (JOSEException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
