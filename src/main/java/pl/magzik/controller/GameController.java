package pl.magzik.controller;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import pl.magzik.model.Game;
import pl.magzik.repository.GameService;

import java.util.Optional;

/**
 * Controller responsible for handling HTTP requests related to games.
 * This controller provides endpoints for retrieving all games and details of a specific game.
 * The games are managed by the {@link GameService}.
 *
 * <p>It includes two main functionalities:</p>
 * <ul>
 *     <li>Retrieving and displaying a list of all games.</li>
 *     <li>Redirecting to a specific game's HTML page when requested by name.</li>
 * </ul>
 */
@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;

    /**
     * Constructs a new {@link GameController} instance with the given {@link GameService}.
     *
     * @param gameService the repository that provides access to the games.
     */
    @Autowired
    public GameController(@NotNull GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles HTTP GET requests to display a list of all games.
     *
     * <p>This method retrieves all games from the {@link GameService} and adds them to the model
     * to be displayed in a Thymeleaf template.</p>
     *
     * @param model the model to populate with the list of games.
     * @return the name of the Thymeleaf template to render, which will display the list of games.
     */
    @NotNull
    @GetMapping("/games")
    public String getAllGames(@NotNull Model model) {
        model.addAttribute("games", gameService.getAllGames().stream().map(Game::name).toList());
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
    @NotNull
    @GetMapping("/games/{name}")
    public String getGame(@NotNull @PathVariable(name = "name") String name) {
        Optional<Game> optionalGame = gameService.findGameByName(name);
        if (optionalGame.isEmpty()) {
            logger.warn("Game {} not found", name);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found.");
        }

        Game game = optionalGame.get();
        return String.format("redirect:/games/%s/%s", game.name(), game.htmlFile());
    }
}
