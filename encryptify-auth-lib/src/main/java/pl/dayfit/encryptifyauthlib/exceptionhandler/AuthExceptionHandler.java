package pl.dayfit.encryptifyauthlib.exceptionhandler;

import com.nimbusds.jwt.proc.ExpiredJWTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(ExpiredJWTException.class)
    private ResponseEntity<Map<String, String>> handleExpiredException()
    {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        Map.of(
                                "error",
                                "Expired JWT token"
                        )
                );
    }
}
