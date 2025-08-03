package pl.magzik.my_hub.exception.game;

public class GameInvalidFormatException extends RuntimeException {

    public GameInvalidFormatException() {
        super("Game of provided format is incorrect.");
    }

}
