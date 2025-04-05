package pl.magzik.my_hub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.dto.GameListResponse;
import pl.magzik.my_hub.dto.StringResponse;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.service.GameService;

import java.io.IOException;
import java.util.List;

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
     * @return A {@link ResponseEntity} containing a {@link GameListResponse} object,
     *         which includes a list of {@link GameDTO} representing the available games.
     * */
    @GetMapping
    public ResponseEntity<?> getAllGames() {
        List<GameDTO> gameList = gameService.findAllGames()
                .stream()
                .map(g -> new GameDTO(g.name(), g.htmlFile()))
                .toList();

        GameListResponse response = new GameListResponse(gameList);

        return ResponseEntity.ok(response);
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
     * @throws ResponseStatusException with {@link HttpStatus#BAD_REQUEST} if the given <b>game</b> is invalid.
     * @throws ResponseStatusException with {@link HttpStatus#INTERNAL_SERVER_ERROR} if the save operation fails.
     * */
    @PostMapping
    public ResponseEntity<StringResponse> addGame(
            @RequestParam MultipartFile game
    ) {
        try {
            gameService.saveGame(game);
            return ResponseEntity.ok(new StringResponse("The game has been successfully uploaded."));
        } catch (IOException e) {
            log.error("Couldn't save provided game files. 'message={}'", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Game upload failure.");
        }
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
     * @throws ResponseStatusException with {@link HttpStatus#NOT_FOUND}
     *                                  if the given <b>gameName</b> doesn't correspond to any game file in the system.
     * @throws ResponseStatusException with {@link HttpStatus#INTERNAL_SERVER_ERROR}
     *                                  if delete operation fails.
     * */
    @DeleteMapping("/{gameName}")
    public ResponseEntity<?> deleteGame(
            @PathVariable String gameName
    ) {
        try {
            gameService.deleteGame(gameName);
            return ResponseEntity.ok(new StringResponse("The game has been successfully deleted."));
        } catch (IOException e) {
            log.error("Couldn't delete provided game files. 'message={}'", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Game deletion failed.");
        }
    }
}
