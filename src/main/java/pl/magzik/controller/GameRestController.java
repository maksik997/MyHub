package pl.magzik.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.magzik.model.Game;
import pl.magzik.service.GameService;

/**
 * REST Controller responsible for handling game-related operations.
 * <p>
 *     Provides endpoints for managing games, including retrieval, uploading, and deletion.
 *     Ensures a structured and secure data exchange through different Data Transfer Object classes of the {@link Game} record.
 * </p>
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
@RestController
@RequestMapping("/api/1.2/games")
public class GameRestController {

    private static final Logger log = LoggerFactory.getLogger(GameRestController.class);

    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllGames() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @PostMapping
    public ResponseEntity<?> addGames() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteGame() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
