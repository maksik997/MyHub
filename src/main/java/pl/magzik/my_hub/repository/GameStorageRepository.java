package pl.magzik.my_hub.repository;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Defines a set of operations crucial in game file storage handling
 *
 * <p>
 *     Operations as follows:
 *     <ol>
 *         <li>{@link GameStorageRepository#save(MultipartFile)}</li>
 *         <li>{@link GameStorageRepository#delete(UUID)}</li>
 *         <li>{@link GameStorageRepository#update(UUID, MultipartFile)}</li>
 *         <li>{@link GameStorageRepository#locateExecutable(UUID)}</li>
 *     </ol>
 * </p>
 *
 * @since 1.3
 *
 * @author Maksymilian Strzelczak
 * @version 1.0
 *
 * @apiNote Please note that any implementation should use some kind of transaction mechanism.
 * */
public interface GameStorageRepository {

    /**
     * Saves/persist provided game files on the filesystem and returns it's .
     * <p>
     *     Please note that the system will throw appropriate exception on failure.
     * </p>
     *
     * @param archive an archive containing game files.
     * @return an identifier (directory name) of extracted and persisted files on the filesystem.
     * @throws pl.magzik.my_hub.exception.game.GameUploadFailureException if game archive couldn't be uploaded and persisted on the filesystem.
     * @throws pl.magzik.my_hub.exception.game.GameInvalidFormatException if provided game archive is invalid in its structure.
     *
     * @since 1.0
     * */
    UUID save(MultipartFile archive);

    /**
     * Gracefully deletes game archive/files under the provided {@link UUID} directory.
     * <p>
     *     Please note that this method won't throw any exception on failure, instead a problem will be logged.
     * </p>
     *
     * @param revId an identifier of the game directory indicating its location on the filesystem.
     *
     * @since 1.0
     * */
    void delete(UUID revId);

    /**
     * Convenience method providing comfortable way of handling updates.
     * <p>
     *     Please note that this method effectively sequentially calls {@link GameStorageRepository#save(MultipartFile)} and {@link GameStorageRepository#delete(UUID)} methods.
     * </p>
     *
     * @param revId an identifier of the current game files on the filesystem.
     * @param archive an archive containing game files.
     * @return an identifier (directory name) of extracted and persisted files on the filesystem.
     * @throws pl.magzik.my_hub.exception.game.GameUploadFailureException if game archive couldn't be uploaded and persisted on the filesystem.
     * @throws pl.magzik.my_hub.exception.game.GameInvalidFormatException if provided game archive is invalid in its structure.
     *
     * @since 1.0
     * */
    default UUID update(UUID revId, MultipartFile archive) {
        var newRevId = save(archive);
        delete(revId);
        return newRevId;
    }

    /**
     * Fetches a single html file location on the filesystem in given {@code revision} directory.
     * <p>
     *     Please note that the system will throw exception when called on not existent game.
     * </p>
     *
     * @param revision an identifier of the game files on the filesystem.
     * @return a path from the relative root directory to the html file in the revision folder.
     * @throws pl.magzik.my_hub.exception.game.GameNotFoundException when called upon not existing game.
     *
     * @since 1.0
     * */
    String locateExecutable(UUID revision);

}
