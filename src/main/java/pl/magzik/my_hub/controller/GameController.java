package pl.magzik.my_hub.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.magzik.my_hub.dto.game.GameDTO;
import pl.magzik.my_hub.service.GameService;

import java.util.List;

/**
 * Controller class for the Game Module.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @since 1.1
 */
@Controller
@RequestMapping("/games")
@Slf4j
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public String getAllGames(Model model) {
        List<String> games = gameService.findAll()
                                        .stream()
                                        .map(GameDTO::name)
                                        .toList();
        model.addAttribute("games", games);
        return "games";
    }

    @PostMapping
    public String addGame() { // todo;
        throw new UnsupportedOperationException("Not implemented.");
    }

    @PostMapping("/{name}")
    public String updateGame() { // todo;
        throw new UnsupportedOperationException("Not implemented.");
    }

    @GetMapping("/{id}")
    public String launchGame(@PathVariable long id) {
        return String.format("redirect:/games/%s",
                gameService.locate(id));
    }

}
