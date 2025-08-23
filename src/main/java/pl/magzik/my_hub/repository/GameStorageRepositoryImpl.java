package pl.magzik.my_hub.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.exception.game.GameDeleteFailureException;
import pl.magzik.my_hub.exception.game.GameLocationFailureException;
import pl.magzik.my_hub.exception.game.GameNotFoundException;
import pl.magzik.my_hub.exception.game.GameUploadFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation of {@link GameStorageRepository}.
 *
 * @version 1.0
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
@Repository
@Slf4j
public class GameStorageRepositoryImpl implements GameStorageRepository {

    @Value("${pl.magzik.my_hub.content.game-directory}")
    private String uploadPath;

    @Override
    public UUID save(MultipartFile archive) {
        Objects.requireNonNull(archive);

        log.debug("Uploading a new game source files.");
        Path temporary = null;
        try {
            // Pre-validate existence of the upload directory.
            Path uploadDir = Path.of(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Pre-validate provided archive
            log.debug("Validating received archive.");
            var originalFileName = archive.getOriginalFilename();
            if (originalFileName == null || !originalFileName.endsWith(".zip")) {
                log.warn("Provided game archive is invalid.");
                throw new GameUploadFailureException("Provided game archive is invalid.");
            }

            // Fetch temporary directory
            log.debug("Fetching temporary directory");
            UUID revisionId = UUID.randomUUID();
            temporary = Files.createTempDirectory(revisionId.toString());

            // Upload the archive to the temporary directory
            log.debug("Uploading archive to the temporary directory");
            Path temporaryArchivePath = temporary.resolve("archive.zip");
            Files.copy(archive.getInputStream(), temporaryArchivePath);

            // Validate the archive
            log.debug("Validating uploaded archive");
            if (!Files.exists(temporaryArchivePath)) {
                log.warn("Could not find uploaded game archive. Archive expected path = '{}'", temporaryArchivePath);
                throw new GameUploadFailureException("Could not find uploaded game archive.");
            }

            // Extract the archive to the temporary directory
            log.debug("Extracting the archive to the temporary directory");
            pl.magzik.my_hub.utils.FileUtils.unzipArchive(temporaryArchivePath, temporary);
            Files.delete(temporaryArchivePath);

            // Validate extracted data
            log.debug("Validating extracted data");
            File[] temporarySubDirs = temporary.toFile().listFiles();
            if (temporarySubDirs == null || temporarySubDirs.length != 1) {
                log.warn("Game source files do not meet expected format requirements. 'temporarySubDirs.length' = {}",
                        temporarySubDirs != null ? temporarySubDirs.length : "none");
                throw new GameUploadFailureException("Game source files do not meet expected format requirements.");
            }
            Path gameSourcePath = temporarySubDirs[0].toPath();
            if (!isGameValid(gameSourcePath.toFile())) {
                log.warn("Game source files do not meet expected format requirements. Structural validation failed.");
                throw new GameUploadFailureException("Game source files do not meet expected format requirements.");
            }

            // Move extracted files to correct destination
            log.debug("Moving extracted game source to the target destination");
            Path temporaryGamePath = temporary.resolve(revisionId.toString());
            Files.move(gameSourcePath, temporaryGamePath); // rename
            Path targetPath = Path.of(uploadPath).resolve(revisionId.toString());
            Files.move(temporaryGamePath, targetPath, StandardCopyOption.ATOMIC_MOVE);

            log.info("Successfully uploaded game. Revision id = '{}'", revisionId);
            return revisionId;
        } catch (IOException e) {
            log.warn("Game upload failed due to I/O error.", e);
            throw new GameUploadFailureException(e);
        } finally {
            // Clean temporary directory if exists
            if (temporary != null && Files.exists(temporary)) {
                FileUtils.deleteQuietly(temporary.toFile());
            }
        }
    }

    @Override
    public void delete(UUID revId) {
        Objects.requireNonNull(revId);

        log.debug("Deleting a game of an revision id = '{}'", revId);
        // Fetch the path.
        Path uploadDirectory = Path.of(uploadPath);
        Path gameSource = locateFile(uploadDirectory, revId.toString());

        // Delete the game
        try {
            FileUtils.deleteDirectory(gameSource.toFile());
            log.info("Successfully deleted game source. Revision = '{}'.", revId);
        } catch (IOException e) {
            log.warn("Failed to delete a game, due to an I/O error.", e);
            throw new GameDeleteFailureException("Failed to delete a game, due to an I/O error.");
        }
    }

    @Override
    public String locateExecutable(UUID revision) {
        Path uploadDirectory = Path.of(uploadPath);
        Path gameSource = locateFile(uploadDirectory, revision.toString());

        return locateFile(gameSource, "^.*\\.html$")
                .getFileName()
                .toString();
    }

    /**
     * Locates file in a given directory.
     * <p>
     *     Please note that this method looks for file directly under {@code source} directory.
     *     So depth of this method is always {@code 1}.
     * </p>
     *
     * @param source a directory path to look for in.
     * @param fileName a name of file to be found.
     * @return a {@link Path} to the specified file.
     * @throws GameNotFoundException if a file could not be found.
     * @throws GameLocationFailureException if a file location fails due to an I/O error.
     * */
    private Path locateFile(Path source, String fileName) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(fileName);

        try (Stream<Path> stream = Files.walk(source, 1)) {
            return stream.filter(f -> f.getFileName()
                                            .toString()
                                            .matches(fileName))
                         .findFirst()
                         .orElseThrow(GameNotFoundException::new);
        } catch (IOException e) {
            log.warn("Game could not be found due to I/O error.");
            throw new GameLocationFailureException("Game could not be found due to I/O error.");
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
        // Moved from the old implementation
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
