package pl.magzik.my_hub.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller providing adaption for the thymeleaf error template.
 *
 * @version 1.0
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
@Controller
public class GeneralErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request,
                              Model model) {
        Object statusCodeObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusCodeObj != null ? Integer.parseInt(statusCodeObj.toString()) : 500;

        HttpStatus statusMessageObj = HttpStatus.resolve(statusCode);
        String statusMessage = statusMessageObj != null ? statusMessageObj.getReasonPhrase() : "Unknown";

        String message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();

        model.addAttribute("status", "%d %s".formatted(statusCode, statusMessage));
        model.addAttribute("message", message);

        return "error";
    }

}
