package pl.dayfit.encryptifycore.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.dayfit.encryptifyauthlib.principal.UserPrincipal;
import pl.dayfit.encryptifycore.dto.*;
import pl.dayfit.encryptifycore.entity.DriveFolder;
import pl.dayfit.encryptifycore.service.FolderManagementService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/folder")
public class FolderManagementController {
    private final FolderManagementService folderManagementService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Long>> createFolder(@RequestBody FolderCreateDto folderCreateDto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        DriveFolder folder = folderManagementService
                .createFolder(folderCreateDto, userPrincipal.getName());

        return ResponseEntity
                .ok(Map.of("id", folder.getId()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteFolder(@RequestBody FolderDeleteDto folderDeleteDto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        folderManagementService
                .deleteFolder(folderDeleteDto.id(), userPrincipal.getName());

        return ResponseEntity
                .ok(Map.of("message", "Folder deleted successfully"));
    }

    @PatchMapping("/rename")
    public ResponseEntity<Map<String, String>> renameFolder(@RequestBody FolderRenameDto  folderRenameDto, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        folderManagementService
                .renameFolder(folderRenameDto,  userPrincipal.getName());

        return ResponseEntity
                .ok(Map.of("message", "Folder renamed successfully"));
    }

    @GetMapping("/get-content")
    public ResponseEntity<List<? extends FileSystemDto>> getFiles(@RequestParam(required = false) Long folderId, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        List<? extends FileSystemDto> dtoList =
                folderManagementService.getContent(
                        userPrincipal.getName(),
                        folderId
                );

        return ResponseEntity.ok(dtoList);
    }
}
