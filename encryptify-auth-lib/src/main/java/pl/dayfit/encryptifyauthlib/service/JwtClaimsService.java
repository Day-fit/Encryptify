package pl.dayfit.encryptifyauthlib.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Locator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.type.JwtTokenType;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtClaimsService {
    private final Locator<Key> keyLocator;

    public boolean isExpired(String token)
    {
        return getExpirationDate(token).before(new Date());
    }

    public Collection<? extends GrantedAuthority> getRoles(String token)
    {
        return extractClaims(token, claims ->
                ((List<?>) claims.get("roles", List.class))
                        .stream()
                        .map(Objects::toString)
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
    }

    public JwtTokenType getTokenType(String token)
    {
        return JwtTokenType.valueOf(
                extractClaims(token, claims -> claims.get("tokenType", String.class))
        );
    }

    public String getSubject(String token)
    {
         String subject = extractClaims(token, Claims::getSubject);

         if (subject == null ||  subject.isBlank())
         {
             throw new BadCredentialsException("Invalid token");
         }

         return subject;
    }

    private Date getExpirationDate(String token)
    {
        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> resolver)
    {
        Claims claims = extractAllClaims(token);
        
        if(claims == null)
        {
            return null;
        }
        
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token)
    {
        try {
            return Jwts.parser()
                    .keyLocator(keyLocator)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch(JwtException ex) {
            throw new BadCredentialsException("Given JWT token is invalid");
        }
    }
}
