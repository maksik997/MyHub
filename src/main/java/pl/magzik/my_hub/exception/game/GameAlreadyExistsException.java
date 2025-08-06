package pl.magzik.my_hub.exception.game;

/**
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
public class GameAlreadyExistsException extends GameException {

    public GameAlreadyExistsException() {
        super("Game of provided details already exists.");
    }

}
