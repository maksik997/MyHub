package pl.magzik.my_hub.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.magzik.my_hub.exception.game.GameException;
import pl.magzik.my_hub.exception.game.GameInvalidFormatException;
import pl.magzik.my_hub.exception.game.GameNotFoundException;

/**
 * A global exception handler providing simple yet effective exception handling.
 *
 * @version 2.1
 * @author Maksymilian Strzelczak
 *
 * @since 1.2
 * */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = GameNotFoundException.class)
    public String handleGameNotFound(RedirectAttributes redirectAttributes) {
        log.warn("Game looked for was not found.");
        redirectAttributes.addFlashAttribute("message",
                "Game could not be found.");
        return "redirect:/games";
    }

    @ExceptionHandler(exception = GameInvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleGameInvalidFormatException(Exception e,
                                                   Model model) {
        log.warn("An exception was thrown due to invalid format of game source files attached.", e);
        handleException(e, model);
        return "error";
    }

    @ExceptionHandler(exception = GameException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGameException(Exception e,
                                      Model model) {
        log.error("An exception was thrown while performing game related operation.", e);
        handleException(e, model);
        return "error";
    }

    private void handleException(Exception e,
                                 Model model) {
        model.addAttribute("message",
                           "An error occurred: %s".formatted(e.getMessage()));
    }

}
