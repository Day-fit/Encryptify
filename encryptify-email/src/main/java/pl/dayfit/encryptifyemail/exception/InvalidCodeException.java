package pl.dayfit.encryptifyemail.exception;

public class InvalidCodeException extends RuntimeException {
    public InvalidCodeException(String message) {
        super(message);
    }
}
