package pl.dayfit.encryptifyauth.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dayfit.encryptifyauth.exception.UserAlreadyExistsException;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex)
    {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex)
    {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("message",
                        "Internal Server Error, Please contact the developer")
        );
    }
}
