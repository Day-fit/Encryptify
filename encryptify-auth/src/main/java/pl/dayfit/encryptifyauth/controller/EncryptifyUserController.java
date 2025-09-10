package pl.dayfit.encryptifyauth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyauth.cacheservice.EncryptifyUserCacheService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/account")
public class EncryptifyUserController {
    private final EncryptifyUserCacheService userCacheService;

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal final Principal principal)
    {
        userCacheService.deleteUserByUsername(principal.getName());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }

    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@AuthenticationPrincipal final Principal principal)
    {
        return ResponseEntity.ok(Map.of("username", principal.getName()));
    }
}
