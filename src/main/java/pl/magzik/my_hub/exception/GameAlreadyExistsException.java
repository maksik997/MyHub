package pl.magzik.my_hub.exception;

public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException(String message) {
        super(message);
    }
}
