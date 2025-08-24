package pl.dayfit.encryptifyauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyauth.service.JwtSecretRotationService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JWKController {
    private final JwtSecretRotationService jwtSecretRotationService;

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getPublicKeys()
    {
        return ResponseEntity.ok(
                jwtSecretRotationService
                        .getPublicKeysAsJWKSet()
                        .toJSONObject()
        );
    }
}
