package pl.magzik.my_hub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.dto.StringResponse;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.service.GameService;

import java.util.List;
import java.util.Objects;

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
        log.info("Game REST controller has been initialized.");
    }

    /**
     * Retrieves all game records stored in the system.
     * <p>
     *     This method fetches game records from {@link GameService}
     *     and returns them as list of {@link GameDTO}, ensuring a safe and structured data representation for the user.
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "content" : [...List<GameDTO>...]
     *          }
     *     }</pre>
     * </p>
     *
     * @return A {@link ResponseEntity} containing a {@link List} of {@link GameDTO} representing the available games.
     * */
    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGames() {
        return ResponseEntity.ok(gameService.findAllGames());
    }

    /**
     * Uploads new game and registers it in the system.
     * <p>
     *     Processes the uploaded game file, stores it securely,
     *     and creates a corresponding game record in the storage system (currently
     *     using file system storage, but may include database storage in the future).
     * </p>
     * <p>
     *     Requirements for the uploaded archive:
     *     <ul>
     *         <li>All games must be uploaded as {@code .zip} archive.</li>
     *         <li>The archive must contain exactly one single top-level directory, <b>inside</b> which there must be exactly one `.html` file.</li>
     *         <li>Game name must be inferred from the archive name (e.g., for archive `Mega_Game.zip` the inferred name will be `Mega Game`)</li>
     *     </ul>
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "message" : "The game has been successfully uploaded."
     *          }
     *     }</pre>
     * </p>
     *
     * @param game A {@link MultipartFile} containing the game `.zip` archive. Must not be null.
     * @return A {@link ResponseEntity} containing a confirmation message upon successful upload.
     * */
    @PostMapping
    public ResponseEntity<StringResponse> addGame(@RequestParam MultipartFile game) {
        // Will throw exception if upload operation fails.
        gameService.saveGame(Objects.requireNonNull(game));
        return ResponseEntity.ok(new StringResponse("The game has been successfully uploaded."));
    }

    /**
     * Deletes a game file from the system.
     * <p>
     *     Removes the game record and deletes the associated file from storage.
     *     This operation is irreversible.
     * </p>
     * <p>
     *     <b>Note:</b> This will remove the entire game directory, including all its files.
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "message" : "The game has been successfully deleted."
     *          }
     *     }</pre>
     * </p>
     *
     * @param gameName The name of the game file to delete. Must not be null.
     * @return A {@link ResponseEntity} containing a confirmation message upon successful deletion.
     * */
    @DeleteMapping("/{gameName}")
    public ResponseEntity<StringResponse> deleteGame(@PathVariable String gameName) {
        // Will throw exception if delete operation fails.
        gameService.deleteGame(Objects.requireNonNull(gameName));
        return ResponseEntity.ok(new StringResponse("The game has been successfully deleted."));
    }
}
