package pl.magzik.my_hub.exception.game;

/**
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
public class GameNotFoundException extends GameException {

  public GameNotFoundException() {
    super("Game of provided credentials could not be found.");
  }

}
