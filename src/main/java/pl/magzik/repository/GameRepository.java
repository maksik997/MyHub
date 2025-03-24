package pl.magzik.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.magzik.model.Game;
import pl.magzik.utils.FileUtils;

import java.io.File;
import java.util.*;

/**
 * Repository class providing methods, to manage {@link Game} objects.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @see Game
 * */
@Repository
public class GameRepository {

    /* TODO:
    *   No.1 - Extent's persistence
    *           Because there is no database - it has to be addressed manually.
    *   No.2 - CRUD operations
    *           Could be a solid step to RESTful API.
    *  */

    private static final Logger log = LoggerFactory.getLogger(GameRepository.class);

    @Value("${game-dir}")
    private String gameDirectory;

    /**
     * Finds a game with specified name.
     * @param name The name of the games.
     * @return An {@link Optional} of the game, or {@link Optional#empty()} if no game found.
     * @throws NullPointerException If given name is null.
     * */
    public Optional<Game> findByName(String name) {
        Objects.requireNonNull(name);

        return FileUtils.getFileStream(gameDirectory, this::isGameValid, Game::of)
                .filter(game -> game.name().equalsIgnoreCase(name))
                .findFirst();
    }

    /**
     * Find all games in the {@link GameRepository#gameDirectory}.
     * @return {@link List} of games found.
     */
    public List<Game> findAll() {
        return FileUtils.getFileStream(gameDirectory, this::isGameValid, Game::of).toList();
    }

    /**
     * Validates a single game directory.
     * A valid game directory contains exactly one `.html` file and may include other assets.
     *
     * @param directory The directory to validate.
     * @return {@code true} if the directory contains exactly one HTML file; {@code false} otherwise.
     * @throws NullPointerException If the provided {@code directory} is {@code null}.
     */
    private boolean isGameValid(File directory) {
        Objects.requireNonNull(directory);

        if (!directory.isDirectory()) {
            log.warn("Directory '{}' is not a directory.", directory);
            return false;
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            log.warn("Directory '{}' is empty.", directory);
            return false;
        }

        long htmlFileCount = Arrays.stream(files)
                .filter(file -> file.getName().endsWith(".html"))
                .count();
        if (htmlFileCount != 1) {
            log.warn("Directory '{}' must contain exactly one HTML file. Found: {}.", directory, htmlFileCount);
            return false;
        }
        return true;
    }
}
