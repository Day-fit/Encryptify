package pl.dayfit.encryptifycore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;
import pl.dayfit.encryptifycore.dto.FileDeleteDto;
import pl.dayfit.encryptifycore.dto.FileRequestDto;
import pl.dayfit.encryptifycore.dto.FileResponseDto;
import pl.dayfit.encryptifycore.service.FileManagementService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @GetMapping("/get-files")
    public ResponseEntity<List<FileResponseDto>> getFiles(@RequestParam(required = false) Long folderId, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        List<FileResponseDto> dtoList =
                fileManagementService.getFiles(
                    userPrincipal.getName(),
                    folderId
                );

        return dtoList.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoList);
    }

    @PostMapping("/upload-file")
    public ResponseEntity<?> handleUploadingFile(@RequestBody FileRequestDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        long id = fileManagementService.handleFileUpload(dto, userPrincipal.getName());
        return ResponseEntity.ok(Map.of("id", id));
    }

    @DeleteMapping("/api/v1/delete-file")
    public ResponseEntity<?> handleDeletingFile(@RequestBody FileDeleteDto dto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        fileManagementService.handleFileDeletion(dto.id(), userPrincipal.getName());
        return ResponseEntity.ok(Map.of("message", "Successfully deleted file"));
    }
}
