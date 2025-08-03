package pl.magzik.my_hub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.exception.development.NotImplementedException;
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
 *         <li>{@link GameService#findAll(Pageable)} - currently optional,</li>
 *         <li>{@link GameService#findById(long)} - currently optional,</li>
 *         <li>{@link GameService#findByName(String)} - marked to delete,</li>
 *         <li>{@link GameService#add()} - currently optional,</li>
 *         <li>{@link GameService#addByName(MultipartFile)} - marked to delete,</li>
 *         <li>{@link GameService#update()} - currently optional,</li>
 *         <li>{@link GameService#delete()} - currently optional,</li>
 *         <li>{@link GameService#deleteByName(String)} - marked to delete,</li>
 *     </ol>
 * </p>
 *
 * @since 1.3
 *
 * @author Maksymilian Strzelczak
 * @version 1.0
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
     * @since tbd
     * */
    default Page<GameDTO> findAll(Pageable pageable) { // todo;
        throw new NotImplementedException();
    }

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
     * @since tbd
     * */
    default GameDTO findById(long id) { // todo;
        throw new NotImplementedException();
    }

    /**
     * Find a specific game by its name.
     * <p>
     *     Please note the system will throw exception if game of the provided name is not found.
     * </p>
     *
     * @param name a name of the game in the system
     * @return a game that matches provided name.
     * @throws GameNotFoundException if there is no game under provided identifier.
     *
     * @since 1.0
     * */
    GameDTO findByName(String name);

    /**
     * Registers a new game in the system.
     * <p>
     *     Please note the system will throw appropriate exception if game files are invalid, corrupted, etc.,
     *     or the game already exists.
     * </p>
     *
     * @throws GameAlreadyExistsException if game of provided details already exists.
     * @throws GameUploadFailureException if game couldn't be uploaded to the server.
     * @throws GameInvalidFormatException if provided game's format is invalid.
     *
     * @since tbd
     * */
    default void add() { // todo;
        throw new NotImplementedException();
    }

    /**
     * Registers a new game in the system.
     * <p>
     *     Please note the system will throw appropriate exception if game files are invalid, corrupted, etc.,
     *     or the game already exists.
     * </p>
     *
     * @param file an {@code zip} archive containing game files.
     * @throws GameAlreadyExistsException if game of provided details already exists.
     * @throws GameUploadFailureException if game couldn't be uploaded to the server.
     * @throws GameInvalidFormatException if provided game's format is invalid.
     *
     * @deprecated <b>Will be removed in future releases.</b>
     * @since 1.0
     * */
    @Deprecated
    void addByName(MultipartFile file);

    /**
     * Updates existing game in the system.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @throws GameNotFoundException if game of the provided details couldn't be found.
     *
     * @since tbd
     * */
    default void update() { // todo;
        throw new NotImplementedException();
    }

    /**
     * Deletes existing game from the system.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @throws GameNotFoundException if game of the provided details couldn't be found.
     *
     * @since tbd
     * */
    default void delete() { // todo;
        throw new NotImplementedException();
    }

    /**
     * Deletes existing game from the system.
     * <p>
     *     Please note the system will throw exception if game is not found.
     * </p>
     *
     * @param fileName a game's name.
     * @throws GameNotFoundException if game of provided name couldn't be found.
     *
     * @since 1.0
     * @deprecated <b>Will be removed in future releases.</b>
     * */
    @Deprecated
    void deleteByName(String fileName);

}
