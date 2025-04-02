package pl.magzik.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Game;
import pl.magzik.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

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

    public void save(MultipartFile gameFile) throws IOException {
        Path gameDirectoryPath = Path.of(gameDirectory);

        // Upload the archive
        String archiveName = gameFile.getOriginalFilename();
        if (archiveName == null || archiveName.isBlank()) {
            log.warn("Provided game archive is invalid, or doesn't exists. 'archive={}'", archiveName);
            throw new IllegalArgumentException("Provided game archive is invalid, or doesn't exists.");
        }
        Path archivePath = gameDirectoryPath.resolve(archiveName);
        if (Files.exists(archivePath)) {
            log.warn("Provided game archive already exists in the system. 'archive={}'", archiveName);
            throw new FileAlreadyExistsException("Provided game archive already exists in the system.");
        }
        Files.copy(gameFile.getInputStream(), archivePath);

        // Create temporary directory
        Path temporaryGameDirectory = gameDirectoryPath.resolve(UUID.randomUUID().toString());
        Files.createDirectories(temporaryGameDirectory);
        FileUtils.unzipArchive(archivePath, temporaryGameDirectory);
        Files.delete(archivePath);

        // Validate extracted game files
        File[] topLevelFiles = temporaryGameDirectory.toFile().listFiles();
        if (topLevelFiles == null || topLevelFiles.length != 1) {
            log.warn("Provided game archive is invalid, or corrupted. 'topLeveFiles.length'={}", topLevelFiles != null ? topLevelFiles.length : "none");
            throw new IllegalArgumentException("Provided game archive is invalid, or corrupted.");
        }
        Path gamePath = topLevelFiles[0].toPath();
        if (!isGameValid(gamePath.toFile())) {
            log.warn("Provide game does not meet format requirements.");
            delete(temporaryGameDirectory);
            throw new IllegalArgumentException("Provided game does not meet format requirements.");
        }

        // Move game files
        String gameName = gamePath.getFileName().toString();
        Files.move(gamePath, gameDirectoryPath.resolve(gameName));
        Files.delete(temporaryGameDirectory);
    }

    public void delete(Game game) throws IOException {
        Objects.requireNonNull(game);
        Path gameDirectoryPath = Path.of(gameDirectory).resolve(game.name());
        delete(gameDirectoryPath);
    }

    private void delete(Path path) throws IOException {
        Objects.requireNonNull(path);
        try (Stream<Path> files = Files.walk(path)) {
            files.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(f -> {
                    try {
                        Files.deleteIfExists(f.toPath());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
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
