package Exceptions;

public class SaldoInsuficienteException extends RuntimeException {
    private String message;

    public SaldoInsuficienteException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
