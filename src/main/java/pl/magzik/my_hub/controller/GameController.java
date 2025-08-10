package pl.magzik.my_hub.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.magzik.my_hub.dto.game.CreateGameRequest;
import pl.magzik.my_hub.service.GameService;

import java.util.stream.Collectors;

/**
 * Controller class for the Game Module.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @since 2.0
 */
@Controller
@RequestMapping("/games")
@Slf4j
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public String getAllGames(Model model) {
        var games = gameService.findAll();
        model.addAttribute("games", games);
        return "games/list";
    }

    @GetMapping("/{id}")
    public String getGame(@PathVariable long id,
                          Model model) {
        var game = gameService.findById(id);
        model.addAttribute("game", game);
        return "games/details";
    }

    @GetMapping("/add")
    public String addGame(Model model) {
        model.addAttribute("request", new CreateGameRequest());
        return "games/add";
    }

    @PostMapping("/add")
    public String addGame(
            @Valid @ModelAttribute CreateGameRequest request,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // In case of errors
            var errors = bindingResult.getFieldErrors() // todo; Extract to method
                                      .stream()
                                      .map(e -> "%s %s".formatted(e.getField(), e.getDefaultMessage()))
                                      .collect(Collectors.joining("\n"));
            model.addAttribute("message", errors);
            model.addAttribute("request", request);
            return "games/add";
        }

        gameService.add(request, file);
        redirectAttributes.addFlashAttribute("message", "Game has been successfully added");
        return "redirect:/games";
    }

    @PostMapping("/{id}/update")
    public String updateGame(@PathVariable long id,
                             @Valid @ModelAttribute CreateGameRequest request,
                             BindingResult bindingResult,
                             @RequestParam("file") MultipartFile file,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // In case of errors
            var game = gameService.findById(id);
            var errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> "%s %s".formatted(e.getField(), e.getDefaultMessage()))
                    .collect(Collectors.joining("\n"));
            model.addAttribute("message", errors);
            model.addAttribute("game", game);
            return "games/details";
        }

        gameService.update(id, request, file);
        redirectAttributes.addFlashAttribute("message", "Game has been successfully updated.");
        return "redirect:/games/%d".formatted(id);
    }

    @PostMapping("/{id}/delete")
    public String deleteGame(@PathVariable long id,
                             RedirectAttributes redirectAttributes) {
        gameService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Game has been successfully deleted.");
        return "redirect:/games";
    }

    @GetMapping("/{id}/play")
    public String launchGame(@PathVariable long id) {
        return String.format("redirect:/games/%s",
                             gameService.locate(id));
    }

}
