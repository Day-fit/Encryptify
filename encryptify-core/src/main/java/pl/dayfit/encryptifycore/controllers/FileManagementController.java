package pl.dayfit.encryptifycore.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifycore.auth.UserPrincipal;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.services.FileManagementService;

@RestController
@RequiredArgsConstructor
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @PostMapping("/api/v1/upload-file")
    public ResponseEntity<?> handleUploadingFile(@RequestBody FileRequestDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        fileManagementService.handleFileUpload(dto, userPrincipal.getName());
        return ResponseEntity.ok().build();
    }
}
