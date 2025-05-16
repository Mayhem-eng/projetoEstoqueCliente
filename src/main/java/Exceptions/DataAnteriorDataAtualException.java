package Exceptions;

public class DataAnteriorDataAtualException extends RuntimeException {
  String message;

  public DataAnteriorDataAtualException(String message) {
    super(message);
    this.message = message;
  }

  @Override
  public String getMessage() {
    return String.format("Data %s anterior a data atual", message);
  }
}
