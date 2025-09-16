package pl.dayfit.encryptifycore.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;
import pl.dayfit.encryptifycore.dto.FileDeleteDto;
import pl.dayfit.encryptifycore.dto.FileRenameDto;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.exception.FileActionException;
import pl.dayfit.encryptifycore.service.FileManagementService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Long>> handleUploadingFile(@RequestPart FileRequestDto metadata, @RequestPart MultipartFile file, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        long id = fileManagementService.handleFileUpload(
                metadata,
                file,
                userPrincipal.getName(),
                userPrincipal.getBucketName()
        );

        return ResponseEntity
                .ok(Map.of("id", id));
    }

    @GetMapping("/download")
    public void handleFileDownload (@RequestParam long fileId, @AuthenticationPrincipal UserPrincipal userPrincipal, HttpServletResponse response)
    {
        response.setContentType("application/octet-stream");

        try (OutputStream out = response.getOutputStream()) {
            fileManagementService.handleFileDownload(fileId, userPrincipal.getName(), userPrincipal.getBucketName(), out);
        } catch (IOException ex) {
            throw new FileActionException("Failed to download a file. Try again later");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> handleDeletingFile(@RequestBody FileDeleteDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        fileManagementService.handleFileDeletion(dto.id(), UUID.fromString(userPrincipal.getName()), userPrincipal.getBucketName());
        return ResponseEntity
                .ok(Map.of("message", "Successfully deleted file"));
    }

    @PatchMapping("/rename")
    public ResponseEntity<Map<String, String>> handleFileRenaming(@RequestBody FileRenameDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        fileManagementService.handleFileRenaming(dto, userPrincipal.getName(), userPrincipal.getBucketName());

        return ResponseEntity
                .ok(Map.of("message", "Successfully renamed file"));
    }
}
