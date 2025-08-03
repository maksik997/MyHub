package pl.magzik.my_hub.exception.game;

public class GameAlreadyExistsException extends RuntimeException {

    public GameAlreadyExistsException() {
        super("Game of given credentials already exists.");
    }

}
