package pl.magzik.repository;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Media;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * A repository for managing media files.
 * Provides functionalities to load, retrieve, and upload media files stored in a directory.
 * */
@Service
public class MediaRepository {
    private static final Logger logger = LoggerFactory.getLogger(MediaRepository.class);

    @Value("${media-dir}")
    private String mediaDirectory;
    
    private Set<Media> media;

    /**
     * Initialises the repository by loading media files from the specified directory.
     */
    @PostConstruct
    public void init() {
        logger.info("Initialising Media repository...");
        this.media = new ConcurrentSkipListSet<>(Comparator.comparing(Media::fileName, String.CASE_INSENSITIVE_ORDER));
        this.media.addAll(findAllMedia());
        logger.info("Loaded: {} media files. Media repository initialised.", media.size());
    }

    /**
     * Returns the directory where media files are stored.
     *
     * @return the media directory path
     */
    public String getMediaDirectory() {
        return mediaDirectory;
    }

    /**
     * Scans the media directory for files and converts them into a list of {@link Media} objects.
     *
     * @return a list of all media files in the directory
     * @throws RuntimeException if the directory does not exist or is invalid
     */
    private List<Media> findAllMedia() {
        File mainDirectory = new File(mediaDirectory);

        if (!mainDirectory.exists() || !mainDirectory.isDirectory()) {
            logger.error("Media directory '{}' doesn't exists or is not a directory.", mediaDirectory);
            throw new RuntimeException("Media directory doesn't exists or is not a directory.");
        }

        return Arrays.stream(Objects.requireNonNull(mainDirectory.listFiles()))
            .map(Media::of)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a paginated list of media files.
     *
     * @param page the page number (starting from 0)
     * @param size the number of media files per page
     * @return a list of media files for the specified page
     */
    public List<Media> getPagedMedia(int page, int size) {
        return media.stream()
            .skip((long) page * size)
            .limit(size)
            .map(m -> new Media(m.fileName(), m.type(), m.maskUrl()))
            .toList();
    }

    /**
     * Retrieves total media files count.
     *
     * @return a total media count.
     * */
    public int getMediaCount() {
        return media.size();
    }

    /**
     * Finds a media file by its filename.
     *
     * @param filename the name of the file to search for
     * @return the {@link Media} object representing the file
     * @throws IllegalArgumentException if the file is not found
     */
    public Media findMediaByFileName(String filename) {
        return media.stream()
            .filter(m -> m.fileName().equals(filename))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Media not found."));
    }

    /**
     * Uploads multiple files to the media directory.
     * Each file is checked for validity, assigned a unique filename if necessary, and added to the repository.
     *
     * @param files the list of files to upload
     * @throws IOException if any file fails to upload or if the media directory cannot be created
     */
    public void uploadFiles(List<MultipartFile> files) throws IOException {
        File uploadDirectory = new File(mediaDirectory);
        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            logger.error("Failed to create media directory: {}", mediaDirectory);
            throw new IOException("Couldn't create media directory: " + mediaDirectory);
        }

        for (var file : files) {
            String filename = file.getOriginalFilename();
            if (!isFileValid(filename)) continue;

            try {
                String uniqueFileName = getUniqueFilename(uploadDirectory, filename);
                File destination = new File(uploadDirectory, uniqueFileName);
                file.transferTo(destination);
                media.add(Media.of(destination));
            } catch (IOException e) {
                logger.error("Failed to upload file: {}", filename, e);
                throw new IOException("Error uploading file: " + filename, e);
            }
        }
    }

    /**
     * Validates the filename of a file.
     *
     * @param filename the filename to validate
     * @return true if the filename is valid, false otherwise
     */
    private boolean isFileValid(String filename) {
        if (filename == null || filename.isEmpty()) {
            logger.warn("File skipped: no original filename provided.");
            return false;
        }
        return true;
    }

    /**
     * Generates a unique filename in the media directory if a file with the same name already exists.
     *
     * @param uploadDirectory the directory where the file will be stored
     * @param filename the original filename
     * @return a unique filename
     */
    private String getUniqueFilename(File uploadDirectory, String filename) {
        File file = new File(uploadDirectory, filename);
        if (file.exists()) {
            String baseName = filename.substring(0, filename.lastIndexOf("."));
            String extension = filename.substring(filename.lastIndexOf("."));
            int ctr = 1;
            while (file.exists()) {
                filename = String.format("%s_%d%s", baseName, ctr++, extension);
                file = new File(uploadDirectory, filename);
            }
        }
        return filename;
    }
}
