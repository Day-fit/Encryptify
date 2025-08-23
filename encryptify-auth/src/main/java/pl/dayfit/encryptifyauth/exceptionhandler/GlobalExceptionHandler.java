package pl.dayfit.encryptifyauth.exceptionhandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dayfit.encryptifyauth.exception.NoUniqueCodeException;
import pl.dayfit.encryptifyauth.exception.UserAlreadyExistsException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExistsException ex)
    {
        log.debug("User tried to create account with username or email that already exists {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex)
    {
        log.debug("An access has been denied for user {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex)
    {
        log.debug("An authentication exception occurred for user {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message",
                        ex.getMessage())
        );
    }

    @ExceptionHandler(NoUniqueCodeException.class)
    public ResponseEntity<?> handleNoUniqueCodeException(NoUniqueCodeException ex)
    {
        log.debug("No unique code has been found for user. {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("An unexpected error occurred while processing the request. {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of("message",
                        "Internal Server Error, Please contact the developer")
        );
    }
}
