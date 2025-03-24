package pl.magzik.controller;

//import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.magzik.model.Media;
import pl.magzik.repository.MediaRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for managing media files.
 * <p>
 * This controller provides endpoints for:
 * <ul>
 *     <li>Retrieving metadata about media files with pagination</li>
 *     <li>Downloading media files</li>
 *     <li>Uploading new media files</li>
 * </ul>
 * <p>
 * Each endpoint is designed to handle specific aspects of media file management,
 * ensuring proper validation, error handling, and user feedback.
 */
@Controller
@RequestMapping("/media")
public class MediaController {
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    private final MediaRepository mediaRepository;

    /**
     * Constructs a {@code MediaController} with the specified {@code MediaRepository}.
     *
     * @param mediaRepository the repository for managing media files
     */
    @Autowired
    public MediaController(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    /**
     * Retrieves a paginated list of media metadata.
     * <p>
     * Adds the list of media metadata to the model and returns the "media" view.
     * Pagination is controlled via query parameters:
     * <ul>
     *     <li>{@code page} - the page number to retrieve (0-based, default: 0)</li>
     *     <li>{@code size} - the number of items per page (default: 10)</li>
     * </ul>
     *
     * @param page  the page number to retrieve (must be non-negative)
     * @param size  the number of items per page (must be positive)
     * @param model the model to populate with media metadata
     * @return the name of the view ("media")
     * @throws ResponseStatusException if {@code page} or {@code size} have invalid values
     */
    /*@NotNull*/
    @GetMapping
    public String getMedia(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        /*@NotNull*/ Model model
    ) {
        if (page < 0 || size <= 0) {
            logger.warn("Provided request have invalid parameters. Page: {}, Size: {}", page, size);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both page and size should have positive or zero value.");
        }

        int totalCount = mediaRepository.getMediaCount();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("media", mediaRepository.getPagedMedia(page, size));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);

        return "media";
    }

    /**
     * Retrieves a specific media file by its filename.
     * <p>
     * This endpoint allows users to download a file stored on the server.
     * If the file does not exist, a 404 status is returned. In case of server errors, a 500 status is returned.
     *
     * @param filename the name of the file to retrieve
     * @return a {@code ResponseEntity} containing the file as a {@code Resource},
     * or an error status if the file is not found or cannot be retrieved
     * @throws ResponseStatusException if the file does not exist or cannot be processed
     */
    /*@NotNull*/
    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> getMediaFile(/*@NotNull*/ @PathVariable(name = "filename") String filename) {
        try {
            Media media = mediaRepository.findMediaByFileName(filename);
            File file = new File(mediaRepository.getMediaDirectory(), media.fileName());

            if (!file.exists()) {
                logger.warn("File not found: {}", filename);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
            }

            Resource resource = new FileSystemResource(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, media.getMimeType())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.fileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error retrieving file: {}", filename, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during media aggregation. Error: " + e.getMessage(), e);
        }
    }

    /**
     * Displays the upload view.
     * <p>
     * Renders a form for uploading files with an optional message indicating the success or failure of an upload operation.
     *
     * @param model the model to populate with an optional message
     * @return the name of the view ("upload")
     */
    /*@NotNull*/
    @GetMapping("/upload")
    public String showUploadView(/*@NotNull*/ Model model) {
        model.addAttribute("message", "");
        return "upload";
    }

    /**
     * Uploads multiple media files to the repository.
     * <p>
     * Handles a POST request to upload one or more files. Displays a success or failure message
     * on the same view after processing the upload.
     *
     * @param files the list of files to upload
     * @param model the model to populate with a message indicating the success or failure of the upload
     * @return the name of the view ("upload")
     */
    /*@NotNull*/
    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(name = "files") List<MultipartFile> files, /*@NotNull*/ Model model) {
        if (files == null || files.isEmpty()) {
            model.addAttribute("message", "No files provided for upload.");
            return "upload";
        }

        try {
            mediaRepository.uploadFiles(files);
            logger.info("Successfully uploaded {} files.", files.size());
            model.addAttribute("message", "File uploaded successfully");
        } catch (IOException e) {
            logger.warn("File upload failed: {}", e.getMessage(), e);
            model.addAttribute("message" ,"File upload failed: " + e.getMessage());
        }

        return "upload";
    }
}
