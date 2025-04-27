package price.tracker.resetcode.exception;

public class InvalidResetCodeException extends RuntimeException {

    public InvalidResetCodeException(String message) {
        super(message);
    }

    public InvalidResetCodeException(String message, Exception exception) {
        super(message, exception);
    }
}
