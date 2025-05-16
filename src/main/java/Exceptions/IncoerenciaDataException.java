package Exceptions;

public class IncoerenciaDataException extends RuntimeException {
    private String message;

    public IncoerenciaDataException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("Incoerencia de dados %s", message);
    }
}
