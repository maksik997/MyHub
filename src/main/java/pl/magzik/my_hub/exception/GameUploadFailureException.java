package pl.magzik.my_hub.exception;

public class GameUploadFailureException extends RuntimeException {
    public GameUploadFailureException(String message) {
        super(message);
    }
}
