package pl.dayfit.encryptifyauth.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
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

    public String generateToken(String username, long expiration, JwtTokenType tokenType)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserByUsername(username);

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", user.getRoles());
        claims.put("tokenType", tokenType.toString());

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
