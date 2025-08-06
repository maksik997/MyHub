package pl.magzik.my_hub.exception.game;

/**
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
public class GameUploadFailureException extends GameException {

    public GameUploadFailureException(String message) {
        super("Game upload failed, due to '%s'.".formatted(message));
    }

    public GameUploadFailureException(Throwable cause) {
        super(cause);
    }

}
