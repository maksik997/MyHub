package pl.magzik.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Media;
import pl.magzik.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Repository class providing methods, to manage {@link Media} objects.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 * @see Media
 * */
@Repository
public class MediaRepository {

    /* TODO:
    *   No.1 Implement all CRUD operations.
    *   No.2 Extent's persistence
    * */

    private static final Logger log = LoggerFactory.getLogger(MediaRepository.class);

    @Value("${media-dir}")
    private String mediaDirectory;

    /**
     * Finds a media file with specified name.
     * @param name The name of the media file.
     * @return An {@link Optional} of the media file, or {@link Optional#empty()} if no game found.
     * @throws NullPointerException If given name is null.
     */
    public Optional<Media> findByName(String name) {
        Objects.requireNonNull(name);
        return FileUtils.getFileStream(mediaDirectory, this::isMediaValid, Media::of)
                .filter(m -> m.fileName().equals(name))
                .findFirst();
    }

    /**
     * Finds all media in the {@link MediaRepository#mediaDirectory}.
     * @return {@link List} of media files found.
     */
    public List<Media> findAll() {
        return FileUtils.getFileStream(mediaDirectory, this::isMediaValid, Media::of)
                .sorted()
                .toList();
    }

    /**
     * Counts media files.
     * @return Number of media files found.
     * */
    public long countAll() {
        return FileUtils.getFileStream(mediaDirectory, this::isMediaValid, Media::of).count();
    }

    /**
     * Saves a list of media files provided.
     * @param files {@link List} of {@link MultipartFile} containing media files provided.
     * @throws NullPointerException If the provided list is {@code null}.
     */
    public void saveAll(List<MultipartFile> files) throws IOException {
        File uploadDirectory = new File(mediaDirectory);
        if (!uploadDirectory.exists()) {
            log.error("Directory '{}' doesn't exists.", uploadDirectory);
            throw new IOException("Media directory doesn't exists");
        }

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) { continue; }

            Path destinationPath = Path.of(mediaDirectory, String.format("%s_%s", UUID.randomUUID(), fileName));
            Files.copy(file.getInputStream(), destinationPath);
        }
    }

    private boolean isMediaValid(File file) {
        Objects.requireNonNull(file);

        if (!file.exists()) {
            log.warn("File '{}' already exists", file);
        }
        return true;
    }
}
