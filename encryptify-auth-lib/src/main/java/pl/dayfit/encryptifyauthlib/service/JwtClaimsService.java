package pl.dayfit.encryptifyauthlib.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.dto.JwtRolesDTO;
import pl.dayfit.encryptifyauthlib.keylocator.KeyLocator;

import java.util.Collection;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@ConditionalOnBooleanProperty(value = "key-listener.enabled")
public class JwtClaimsService {
    private final KeyLocator keyLocator;

    public boolean isExpired(String token)
    {
        return getExpirationDate(token).before(new Date());
    }

    public Collection<? extends GrantedAuthority> getRoles(String token)
    {
        return extractClaims(token, claims -> claims.get("roles", JwtRolesDTO.class)
                .roles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
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
