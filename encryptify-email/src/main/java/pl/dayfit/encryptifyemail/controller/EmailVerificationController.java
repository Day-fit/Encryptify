package pl.dayfit.encryptifyemail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyemail.service.EmailVerificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String code)
    {
        emailVerificationService.handleCodeVerification(code);

        return ResponseEntity.ok(
                Map.of("message",
                        "Account verification has been successfully completed. Welcome aboard.")
        );
    }
}
