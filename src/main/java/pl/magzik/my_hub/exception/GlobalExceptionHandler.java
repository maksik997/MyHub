package pl.magzik.my_hub.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A global exception handler providing simple yet effective exception handling.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, Model model) {
        log.warn("Unexpected error has occurred class='{}', message='{}'\n=== Stack trace: ===", e.getClass(), e.getMessage(), e);
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", "Unexpected error has occurred '%s'.".formatted(e.getMessage()));
        return "error";
    }

}
