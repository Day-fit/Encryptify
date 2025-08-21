package pl.dayfit.encryptifyemail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dayfit.encryptifyemail.service.VerificationService;

import java.security.InvalidKeyException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class VerificationController {
    private final VerificationService verificationService;

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAccount(@RequestParam String code) throws InvalidKeyException
    {
        verificationService.handleVerification(code);

        return ResponseEntity
                .ok(
                        Map.of("message", "Account successfully verified")
                );
    }
}
