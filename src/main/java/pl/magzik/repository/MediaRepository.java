package pl.magzik.repository;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Media;
import pl.magzik.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * A repository for managing media files.
 * Provides functionalities to load, retrieve, and upload media files stored in a directory.
 * */
@Service
public class MediaRepository {

    /* TODO:
    *   No.1 Implement all CRUD operations.
    * */

    private static final Logger log = LoggerFactory.getLogger(MediaRepository.class);

    @Value("${media-dir}")
    private String mediaDirectory;

    /**
     * TODO...
     */
    public List<Media> findAll() {
        return FileUtils.getFileStream(mediaDirectory, this::isMediaValid, Media::of).toList();
    }

    /**
     * TODO...
     */
    public Media findByName(String name) {
        return FileUtils.getFileStream(mediaDirectory, this::isMediaValid, Media::of)
                .filter(m -> m.fileName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Media not found."));
    }

    /**
     * TODO...
     */
    /* TODO: REFACTOR */
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
