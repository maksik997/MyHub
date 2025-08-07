package pl.magzik.my_hub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.game.CreateGameRequest;
import pl.magzik.my_hub.dto.game.GameDTO;
import pl.magzik.my_hub.service.GameService;

import java.util.List;

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
public class GameController { // todo;

    private final GameService gameService;

    @GetMapping
    public String getAllGames(Model model) {
        var games = gameService.findAll();
        model.addAttribute("games", games);
        return "games/list";
    }

    @GetMapping("/add")
    public String addGame(Model model) {

        model.addAttribute("request", new CreateGameRequest());

        return "games/add";
    }

    @PostMapping("/add")
    public String addGame(
            @ModelAttribute CreateGameRequest request,
            @RequestParam("file") MultipartFile file,
            Model model) {

        gameService.add(request, file);

        return "redirect:/games";
    }

    @PostMapping("/{name}")
    public String updateGame() { // todo;
        throw new UnsupportedOperationException("Not implemented.");
    }

    @PostMapping("/{id}/delete")
    public String deleteGame(@PathVariable long id) {
        gameService.delete(id);
        return "redirect:/games";
    }

    @GetMapping("/{id}")
    public String launchGame(@PathVariable long id) {
        return String.format("redirect:/games/%s",
                gameService.locate(id));
    }

}
