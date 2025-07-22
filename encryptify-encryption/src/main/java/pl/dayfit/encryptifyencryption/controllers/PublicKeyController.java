package pl.dayfit.encryptifyencryption.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifyencryption.dto.PublicKeyUploadDto;
import pl.dayfit.encryptifyencryption.services.PublicKeyService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PublicKeyController {
    private final PublicKeyService publicKeyService;

    @PostMapping
    public ResponseEntity<Map<String, String>> handlePublicKeyUpload(@RequestBody @Valid PublicKeyUploadDto publicKeyUploadDto) {
        publicKeyService.assignPublicKey(publicKeyUploadDto);

        return ResponseEntity.ok(Map.of("message","successfully uploaded public key"));
    }
}
