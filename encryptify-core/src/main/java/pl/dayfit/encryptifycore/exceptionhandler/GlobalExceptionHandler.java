package pl.dayfit.encryptifycore.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dayfit.encryptifycore.exception.FailedToCreateFileException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FailedToCreateFileException.class)
    public ResponseEntity<Map<String, String>> handleFailedToCreateFileException(FailedToCreateFileException ignored)
    {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to Create File"));
    }
}
