package pl.dayfit.encryptifyencryption.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidatorFail(MethodArgumentNotValidException ex)
    {
        //TODO: implement more robust logic
        return ResponseEntity
                .badRequest()
                .body(
                        Map.of("message", ex.getBindingResult().getFieldError().getDefaultMessage())
                );
    }
}
