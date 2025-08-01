package pl.dayfit.encryptifycore.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.dayfit.encryptifycore.auth.UserPrincipal;
import pl.dayfit.encryptifycore.dto.FileDeleteDto;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.services.FileManagementService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @PostMapping("/api/v1/upload-file")
    public ResponseEntity<?> handleUploadingFile(@RequestBody FileRequestDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        fileManagementService.handleFileUpload(dto, userPrincipal.getName());
        return ResponseEntity.ok(Map.of("message", "Successfully uploaded file"));
    }

    @DeleteMapping("/api/v1/delete-file")
    public ResponseEntity<?> handleDeletingFile(@RequestBody FileDeleteDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        fileManagementService.handleFileDeletion(dto.id(), userPrincipal.getName());
        return ResponseEntity.ok(Map.of("message", "Successfully deleted file"));
    }
}
