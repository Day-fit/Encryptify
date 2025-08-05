package pl.dayfit.encryptifyauth.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pl.dayfit.encryptifyauthlib.dto.JwtRolesDTO;
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

    public String generateToken(long id, long expiration, JwtTokenType tokenType)
    {
        EncryptifyUser user = encryptifyUserCacheService.getUserById(id);

        Map<String, Object> claims = new HashMap<>();

        claims.put("roles", new JwtRolesDTO(user.getRoles().stream().map(GrantedAuthority::getAuthority).toList()));
        claims.put("tokenType", tokenType.toString());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .signWith(jwtSecretRotationService.getCurrentPrivateKey())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuedAt(new Date())
                .claims(claims)
                .subject(String.valueOf(id))
                .header()
                .add("sk_id", jwtSecretRotationService.getCurrentIndex())
                .and()
                .compact();
    }
}
