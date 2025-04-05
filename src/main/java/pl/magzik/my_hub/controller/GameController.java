package pl.magzik.my_hub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.service.GameService;

import java.util.List;
import java.util.Optional;

/**
 * Controller class shares various endpoints regarding {@link Game} handling.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @see Game
 * @see GameService
 */
@Controller
@RequestMapping("/games")
public class GameController {

    /* TODO:
    *   No.1 Transition to RESTful API.
    * */

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;


    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles HTTP GET requests to display a list of all games.
     *
     * @param model the model to populate with the list of games.
     * @return the name of the Thymeleaf template to render, which will display the list of games.
     */
    @GetMapping
    public String getAllGames(Model model) {
        List<String> games = gameService.findAllGames()
                                        .stream()
                                        .map(Game::name)
                                        .toList();
        model.addAttribute("games", games);
        return "games";
    }

    /**
     * Handles HTTP GET requests for a specific game identified by its name.
     *
     * <p>This method searches for the game by its name using the {@link GameService}. If the game is found,
     * it redirects to the game's HTML page. If the game is not found, it throws a {@link ResponseStatusException}
     * with a {@link HttpStatus#NOT_FOUND} status.</p>
     *
     * @param name the name of the game to retrieve.
     * @return a redirection URL to the game's HTML page if the game is found.
     * @throws ResponseStatusException if the game with the specified name is not found, returning a 404 Not Found status.
     */
    @GetMapping("/{name}")
    public String launchGame( @PathVariable(name = "name") String name) {
        Optional<Game> optionalGame = gameService.findGameByName(name);
        if (optionalGame.isEmpty()) {
            log.warn("Game '{}' not found", name);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        }

        Game game = optionalGame.get();
        return String.format("redirect:/games/%s/%s", game.name(), game.htmlFile());
    }
}
