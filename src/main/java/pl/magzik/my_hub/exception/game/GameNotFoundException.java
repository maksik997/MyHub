package pl.magzik.my_hub.exception.game;

public class GameNotFoundException extends RuntimeException {

  public GameNotFoundException() {
    super("Game of provided credentials could not be found.");
  }

}
