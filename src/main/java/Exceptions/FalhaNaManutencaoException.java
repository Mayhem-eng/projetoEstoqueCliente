package Exceptions;

public class FalhaNaManutencaoException extends Exception {
  private final String message;

  public FalhaNaManutencaoException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return String.format("FALHA NA MANUTENCAO: %s", message);
  }
}
