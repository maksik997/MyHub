package pl.magzik.my_hub.exception.game;

/**
 * Generic exception indicating a problem related to Game Module.
 *
 * @author Maksymilian Strzelczak
 * @version 1.0
 *
 * @since 1.3
 *
 * @apiNote Please <b>note</b>: This exception should not be used standalone.
 *              It should be used as generalisation of the more descriptive and specific instances
 * */
public class GameException extends RuntimeException {

    public GameException() {
    }

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameException(Throwable cause) {
        super(cause);
    }

    public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
