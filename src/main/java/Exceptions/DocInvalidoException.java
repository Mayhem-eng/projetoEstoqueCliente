package Exceptions;

public class DocInvalidoException extends Exception {
    private String message;

    public DocInvalidoException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return String.format("DOCUMENTO %s NAO ATENDE OS CRITERIOS DE VALIDACAO", message);
    }
}
