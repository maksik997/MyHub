package pl.magzik.my_hub.exception;

public class GameNotFoundException extends RuntimeException {
  public GameNotFoundException(String message) {
    super(message);
  }
}
