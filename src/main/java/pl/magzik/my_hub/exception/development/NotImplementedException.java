package pl.magzik.my_hub.exception.development;

import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * Exception defined as a convince method to handle unimplemented pieces of code.
 * <p>
 *     As a side effect of throwing this exception log message will be printed.
 * </p>
 *
 * @since 1.3
 * @author Maksymilian Strzelczak
 *
 * @apiNote Constructor's JavaDoc is copied from {@link UnsupportedOperationException} api.
 * */
@Slf4j
public class NotImplementedException extends UnsupportedOperationException {

    @Serial
    private static final long serialVersionUID = -1269696969420696969L;

    /**
     * Constructs an NotImplementedException with no detail message.
     * */
    public NotImplementedException() {
        super();
        log();
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link Throwable#getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A {@code null} value
     *         is permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
        log();
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link Throwable#getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public NotImplementedException(Throwable cause) {
        super(cause);
        log();
    }

    /**
     * Constructs an NotImplementedException with the specified detail message.
     *
     * @param message the detail message
     * */
    public NotImplementedException(String message) {
        super(message);
        log();
    }

    /**
     * Logs information that this exception has been thrown via {@code Slf4j} api.
     *
     * <p>
     *     Convince method.
     * </p>
     * */
    private void log() {
        log.warn("An attempt to use unimplemented operation has been made.");
    }

}
