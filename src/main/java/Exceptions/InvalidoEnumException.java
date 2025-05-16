package Exceptions;

public class InvalidoEnumException extends RuntimeException {
    private String message;

    public InvalidoEnumException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
