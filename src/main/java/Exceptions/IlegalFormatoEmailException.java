package Exceptions;

public class IlegalFormatoEmailException extends RuntimeException {
  private String message;

    public IlegalFormatoEmailException(String message) {
        super(message);
        this.message = message;
    }

  @Override
  public String getMessage() {
    return String.format("Email deve ser informado corretamente(@gmail.com ou .com.br): %s", message);
  }
}
