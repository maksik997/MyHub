package pl.magzik.my_hub.exception.game;

/**
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
public class GameInvalidFormatException extends GameException {

    public GameInvalidFormatException() {
        super("Game of provided format is incorrect.");
    }

}
