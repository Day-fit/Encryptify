package pl.dayfit.encryptifyauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EncryptifyUserController {
    private final EncryptifyUserCacheService userCacheService;

    @PostMapping("/api/v1/delete-account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal final Principal principal)
    {
        userCacheService.deleteUserByUsername(principal.getName());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
