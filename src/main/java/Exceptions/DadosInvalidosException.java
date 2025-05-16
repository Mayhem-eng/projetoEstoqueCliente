package Exceptions;

public class DadosInvalidosException extends RuntimeException {
    private String message;

    public DadosInvalidosException(String message) {
        super(message);
        this.message = message;
    }
}
