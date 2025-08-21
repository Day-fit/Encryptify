package pl.dayfit.encryptifyauth.exception;

public class NoUniqueCodeException extends RuntimeException {
    public NoUniqueCodeException(String message) {
        super(message);
    }
}
