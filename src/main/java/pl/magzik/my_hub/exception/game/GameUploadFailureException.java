package pl.magzik.my_hub.exception.game;

public class GameUploadFailureException extends RuntimeException {

    public GameUploadFailureException(String message) {
        super("Game upload failed, due to '%s'.".formatted(message));
    }

}
