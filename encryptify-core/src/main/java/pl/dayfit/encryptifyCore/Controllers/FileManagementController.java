package pl.dayfit.encryptifyCore.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileManagementController {
    @PostMapping("/api/v1/upload-file")
    public ResponseEntity<?> handleUploadingFile()
    {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
