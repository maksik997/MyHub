package pl.magzik.my_hub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.game.CreateGameRequest;
import pl.magzik.my_hub.dto.game.GameDTO;
import pl.magzik.my_hub.exception.game.GameAlreadyExistsException;
import pl.magzik.my_hub.exception.game.GameInvalidFormatException;
import pl.magzik.my_hub.exception.game.GameNotFoundException;
import pl.magzik.my_hub.exception.game.GameUploadFailureException;

import java.util.List;

/**
 * Defines set of operation available on Game module.
 *
 * <p>
 *     Please note that all implementations should fulfill all the contracts contained in this interface.
 * </p>
 *
 * <p>
 *     Operations as follows:
 *     <ol>
 *         <li>{@link GameService#findAll()},</li>
 *         <li>{@link GameService#findAll(Pageable)},</li>
 *         <li>{@link GameService#findById(long)},</li>
 *         <li>{@link GameService#add(CreateGameRequest,MultipartFile)},</li>
 *         <li>{@link GameService#update(long,CreateGameRequest,MultipartFile)},</li>
 *         <li>{@link GameService#delete(long)},</li>
 *         <li>{@link GameService#locate(long)},</li>
 *     </ol>
 * </p>
 *
 * @since 1.3
 *
 * @author Maksymilian Strzelczak
 * @version 1.4
 * */
public interface GameService {

    /**
     * Fetches all the games available and registered in the system.
     * <p>
     *     Please note, if there is no games available method will return empty list.
     * </p>
     *
     * @return a list of all games available in the system.
     *
     * @since 1.0
     * */
    List<GameDTO> findAll();

    /**
     * Fetches all the games available and registered in the system but also paginated.
     * <p>
     *     Please note, if there is no games available method will return empty page.
     * </p>
     *
     * @param pageable a pagination context (i.e., page number, size of the page etc.)
     * @return a page constructed basing on pagination context containing games available in the system.
     *
     * @since 1.1
     * */
    Page<GameDTO> findAll(Pageable pageable);

    /**
     * Fetches a specific game by its id.
     * <p>
     *     Please note the system will throw exception if game of the provided id is not found.
     * </p>
     *
     * @param id an identifier of the game in the system.
     * @return a game that matches provided identifier.
     * @throws GameNotFoundException if there is no game under provided identifier.
     *
     * @since 1.1
     * */
    GameDTO findById(long id) throws GameNotFoundException;

    /**
     * Registers a new game in the system.
     * <p>
     *     Please note the system will throw appropriate exception if game files are invalid, corrupted, etc.,
     *     or the game already exists.
     * </p>
     *
     * @param request a dto containing game details
     * @param file a file contain game source file archive.
     * @throws GameAlreadyExistsException if game of provided details already exists.
     * @throws GameUploadFailureException if game couldn't be uploaded to the server.
     * @throws GameInvalidFormatException if provided game's format is invalid.
     *
     * @since 1.1
     * */
    void add(CreateGameRequest request,
             MultipartFile file) throws GameAlreadyExistsException,
                                        GameUploadFailureException,
                                        GameInvalidFormatException;

    /**
     * Updates existing game in the system.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @param id an identifier of the game.
     * @param request an updated details about the game
     * @param file a game source file archive. Optional.
     * @throws GameNotFoundException if game of the provided details couldn't be found.
     * @throws GameUploadFailureException if game couldn't be uploaded to the server.
     * @throws GameInvalidFormatException if provided game's format is invalid.
     *
     * @since 1.1
     * */
    void update(long id,
                CreateGameRequest request,
                MultipartFile file) throws GameNotFoundException,
                                           GameUploadFailureException,
                                           GameInvalidFormatException;

    /**
     * Deletes existing game from the system.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @param id an identifier of the game.
     * @throws GameNotFoundException if game of the provided details couldn't be found.
     *
     * @since 1.1
     * */
    void delete(long id) throws GameNotFoundException;

    /**
     * Retrieves game source location path.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @param id an identifier of the game.
     * @throws GameNotFoundException if game of the provided id could not be found.
     *
     * @since 1.1
     * */
    String locate(long id) throws GameNotFoundException;

}
