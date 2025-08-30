package pl.dayfit.encryptifyauthlib.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class JwtClaimsService {
    @Setter
    private Supplier<JWKSet> jwkSetSupplier;

    public Collection<? extends GrantedAuthority> getRoles(String token)
    {
        return extractClaim(token, claims ->
                ((List<?>) claims.getClaim("roles"))
                        .stream()
                        .map(Objects::toString)
                        .map(SimpleGrantedAuthority::new)
                        .toList());
    }

    public JwtTokenType getTokenType(String token)
    {
        return JwtTokenType.valueOf(
                extractClaim(token, claims -> {
                    try {
                        return claims.getStringClaim("tokenType");
                    } catch (ParseException e) {
                        throw new BadCredentialsException("Invalid token type");
                    }
                })
        );
    }

    public String getSubject(String token)
    {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver)
    {
        try
        {
            SignedJWT jwt = (SignedJWT) JWTParser.parse(token);
            JWKSet set = jwkSetSupplier.get();
            JWSHeader header = jwt.getHeader();

            OctetKeyPair publicKey = set.getKeyByKeyId(header.getKeyID()).toOctetKeyPair();
            JWSVerifier verifier = new Ed25519Verifier(publicKey);

            if(!jwt.verify(verifier))
            {
                throw new BadCredentialsException("Invalid token");
            }

            return claimsResolver.apply(
                    jwt.getJWTClaimsSet()
            );
        } catch (ParseException ex) {
            throw new BadCredentialsException("Invalid token", ex);
        } catch (JOSEException ex) {
            throw new IllegalStateException("Internal server error", ex);
        }
    }
}
