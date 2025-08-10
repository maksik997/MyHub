package pl.magzik.my_hub.exception;

import lombok.extern.slf4j.Slf4j;
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
 * @version 2.0
 * @author Maksymilian Strzelczak
 *
 * @since 1.2
 * */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /*@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception e, Model model) {
        log.error("Unspecified unexpected error has occurred!", e);

        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("message", "Unexpected error has occurred '%s'.".formatted(e.getMessage()));
        return "error";
    }*/

}
