package pl.dayfit.encryptifyemail.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyemail.type.JwtTokenType;

import java.security.Key;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtClaimsService {
    private final Locator<Key> keyLocator;

    /**
     * Checks if given email verification token is valid
     * @param token represents JWT email verification token
     * @return true if token type is JwtTokenType.EMAIL_VERIFICATION
     * @throws IllegalArgumentException if token is invalid (Expired, Invalid signature, etc.)
     */
    public boolean isEmailVerificationToken(String token)
    {
        return getTokenType(token) == JwtTokenType.EMAIL_VERIFICATION;
    }

    private JwtTokenType getTokenType(String token) throws IllegalArgumentException
    {
        return JwtTokenType.valueOf(
                extractClaims(token, claims -> claims.get("tokenType", String.class))
        );
    }

    public String getSubject(String token)
    {
         String subject = extractClaims(token, Claims::getSubject);

         if (subject == null || subject.isBlank())
         {
             throw new IllegalArgumentException("Subject of the token was blank or null");
         }

         return subject;
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver) throws IllegalArgumentException
    {
        Claims claims = extractAllClaims(token);
        
        if(claims == null)
        {
            return null;
        }
        
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) throws IllegalArgumentException
    {
        try {
            return Jwts.parser()
                    .keyLocator(keyLocator)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            throw new IllegalArgumentException("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            throw new IllegalArgumentException("Given token is expired");
        } catch(JwtException ex) {
            throw new IllegalArgumentException("Invalid email verification token");
        }
    }
}
