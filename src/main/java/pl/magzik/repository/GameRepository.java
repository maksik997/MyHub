package pl.magzik.repository;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import pl.magzik.model.Game;

import java.io.File;
import java.util.*;

/**
 * Service responsible for managing HTML-based games.
 * It loads game metadata from a specified directory on server startup.
 * Each game is represented by a directory containing exactly one HTML file.
 * Games are loaded and stored in memory for later retrieval.
 * */
@Repository
public class GameRepository {

    /* TODO:
    *   No.1 - Extent's persistence
    *           Because there is no database - it has to be addressed manually
    *  */

    private static final Logger logger = LoggerFactory.getLogger(GameRepository.class);

    @Value("${game-dir}")
    private String gameDirectory;

    @Deprecated // Possible memory usage optimization.
    private List<Game> games;

    /**
     * Initializes the repository by loading games from the specified directory.
     * This method is called automatically after the service is constructed and all dependencies are injected.
     * If the directory is valid, it will load all the games into memory.
     */
    @PostConstruct
    @Deprecated
    public void init() {
        logger.info("Initialising Game repository...");
        this.games = findAll();
        logger.info("Loaded: {} games. Game repository initialised.", games.size());
    }

    /**
     * Retrieves all loaded games.
     * The list is a copy of the games currently managed by the repository, ensuring immutability.
     *
     * @return A list of {@link Game} objects currently managed by this repository.
     */
    /*@NotNull
    @Contract("-> new")*/
    @Deprecated
    public List<Game> getAllGames() {
        return List.copyOf(games);
    }

    /**
     * Finds a game by its name.
     * This method searches the list of loaded games for the specified name.
     *
     * @param name The name of the game to search for.
     * @return An {@link Optional} containing the game if found, or an empty {@link Optional} if no game matches the name.
     */
    /*@NotNull*/
    @Deprecated
    public Optional<Game> findGameByName(/*@NotNull*/ String name) {
        return games.stream()
            .filter(g -> g.name().equals(name))
            .findFirst();
    }

    public Optional<Game> findById(UUID id) {
        return games.stream()
                .filter(game -> game.id().equals(id))
                .findFirst();
    }

    /**
     * Finds all HTML-based games available in the specified {@link #gameDirectory}.
     * Each game is represented by a subdirectory containing exactly one `.html` file.
     *
     * @return A list of {@link Game} objects discovered in the directory.
     *         If the directory is invalid or empty, an empty list is returned.
     */
    public List<Game> findAll() {
        File mainDirectory = new File(gameDirectory);

        if (!isMainDirectoryValid(mainDirectory)) return new ArrayList<>();

        File[] files = mainDirectory.listFiles();
        assert files != null; // Checked via isMainDirectoryValid(...) method.

        return Arrays.stream(files)
            .filter(File::isDirectory)
            .filter(this::isGameDirectoryValid)
            .map(Game::of)
            .toList();
    }

    /**
     * Validates the main game directory.
     * A valid directory exists, is a directory, and contains at least one subdirectory.
     *
     * @param mainDirectory The directory to validate.
     * @return {@code true} if the directory exists, is a directory, and contains at least one subdirectory;
     *         {@code false} otherwise.
     * @throws NullPointerException If the provided {@code mainDirectory} is {@code null}.
     * @throws IllegalStateException If the provided {@code mainDirectory} doesn't exist, or isn't a directory, or does not allow reading.
     */
    private boolean isMainDirectoryValid(/*@NotNull*/ File mainDirectory) {
        Objects.requireNonNull(mainDirectory, "Path to mainDirectory is null.");
        if (!mainDirectory.exists() || !mainDirectory.isDirectory()) {
            logger.error("Directory '{}' doesn't exist or is not a directory.", mainDirectory.getAbsolutePath());
            throw new IllegalStateException("Game directory doesn't exists or is not a directory.");
        }

        File[] files = mainDirectory.listFiles();
        if (files == null) {
            logger.error("Couldn't read directory {}.", mainDirectory.getAbsolutePath());
            throw new IllegalStateException("Unable to read files from directory: " + mainDirectory);
        }

        if (files.length == 0) {
            logger.warn("Directory {} is empty.", mainDirectory.getAbsolutePath());
            return false;
        }

        return true;
    }

    /**
     * Validates a single game directory.
     * A valid game directory contains exactly one `.html` file and may include other assets.
     *
     * @param directory The directory to validate.
     * @return {@code true} if the directory contains exactly one HTML file; {@code false} otherwise.
     * @throws NullPointerException If the provided {@code directory} is {@code null}.
     */
    private boolean isGameDirectoryValid(/*@NotNull*/ File directory) {
        Objects.requireNonNull(directory, "Path to directory is null.");
        File[] subFiles = directory.listFiles();

        if (subFiles == null || subFiles.length == 0) {
            logger.warn("Directory '{}' is empty.", directory.getAbsolutePath());
            return false;
        }

        long htmlFileCount = Arrays.stream(subFiles)
            .filter(file -> file.getName().endsWith(".html"))
            .count();

        if (htmlFileCount != 1) {
            logger.warn("Directory '{}' must contain exactly one HTML file. Found: {}.", directory.getAbsolutePath(), htmlFileCount);
            return false;
        }

        return true;
    }
}
