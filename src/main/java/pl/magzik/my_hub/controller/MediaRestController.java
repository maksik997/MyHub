package pl.magzik.my_hub.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.magzik.my_hub.dto.*;
import pl.magzik.my_hub.model.Media;
import pl.magzik.my_hub.service.MediaService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * REST Controller responsible for handling media-related operations.
 * <p>
 *     Provides endpoints for managing media files, including retrieval, uploading, and deletion.
 *     Ensures a structured and secure data exchange through different Data Transfer Object of the {@link Media} record.
 * </p>
 *
 * @since 1.2
 * @author Maksymilian Strzelczak
 * */
@RestController
@RequestMapping("/api/1.2/media")
public class MediaRestController {

    private static final Logger log = LoggerFactory.getLogger(MediaRestController.class);

    private final MediaService mediaService;

    @Autowired
    public MediaRestController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    /**
     * Retrieves all media records stored in the system.
     * <p>
     *      This method fetches media records from {@link MediaService}
     *      and returns them as a paginated list of {@link MediaDTO},
     *      ensuring a safe and structured data representation for the user.
     * </p>
     * <p>
     *     This method supports pagination. The user should provide the following query parameters:
     *     <ul>
     *         <li><b>page</b> - The index of the requested page (zero-based). Defaults to {@code 0}.</li>
     *         <li><b>size</b> - The number of media items per page. Defaults to {@code 10}.</li>
     *     </ul>
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *      {
     *          "content": [...],
     *          "page": 0,
     *          "size": 10,
     *          "totalPages": 3,
     *          "totalElements": 25
     *      }
     *     }</pre>
     * </p>
     *
     * @param page Index of the requested page (zero-based). Defaults to {@code 0}. Must be non-negative.
     * @param size Number of items per page. Defaults to {@code 10}. Must be positive.
     * @return A {@link ResponseEntity} containing a paginated list of media wrapped in {@link MediaDTO},
     *         along with pagination metadata (current page, size, total page count, total count).
     *         All wrapped in the {@link PaginatedMediaResponse}.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#BAD_REQUEST}
     *         if <b>page</b> is negative or <b>size</b> is zero/negative.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#NOT_FOUND}
     *         if <b>page</b> is greater than the total available pages.
     * */
    @GetMapping
    public ResponseEntity<PaginatedMediaResponse> getAllMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Getting all media available in the system.");
        if (page < 0 || size <= 0) {
            log.warn("Invalid request parameters. 'page={}'; 'size={}'", page, size);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters.");
        }

        long totalElements = mediaService.countAllMedia();
        int totalPages = (int) ((totalElements + size - 1) / size); // equivalent of `(int) Math.ceil((double) totalElements/size)`
        if (totalPages == 0) { totalPages = 1; }
        if (page != 0 && page >= totalPages) {
            log.warn("Requested page out of bounds. 'page={}'; 'totalPages={}'", page, totalPages);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested page out of bounds.");
        }

        List<MediaDTO> mediaList = mediaService.findAllMedia(page, size)
                .stream()
                .map(m -> new MediaDTO(m.fileName(), m.type()))
                .toList();

        PaginatedMediaResponse response = new PaginatedMediaResponse(mediaList, page, size, totalPages, totalElements);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves and serves a media file as a downloadable resource.
     * <p>
     *     Searches for a media file associated with the given file name in {@link MediaService}.
     *     If found, returns the file as a {@link Resource} class with appropriate headers,
     *     allowing the client to download it.
     * </p>
     * <p>
     *     This method sets HTTP headers such as:
     *     <ul>
     *         <li>{@code Content-Type} - dynamically set based on the file type  (e.g., "image/jpeg", "video/mp4").</li>
     *         <li>{@code Content-Disposition} - statically set to "attachment" to trigger download behavior.</li>
     *     </ul>
     * </p>
     *
     * @param fileName The name of the media file to retrieve. Must not be null.
     * @return A {@link ResponseEntity} containing a {@link Resource} with the media file resource.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#NOT_FOUND}
     *                                  if <b>fileName</b> does not correspond to any existing file.
     * */
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getMedia(
            @PathVariable String fileName
    ) {
        log.debug("Downloading media file...");
        Media media = mediaService.findMediaByName(fileName) ///< Handles file existence check
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File '%s' doesn't exist".formatted(fileName)));
        File file = new File(media.path());
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, media.getMimeType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(resource);
    }

    /**
     * Uploads new media files and registers them in the system.
     *
     * <p>
     *     Processes the uploaded media files, stores them securely,
     *     and creates corresponding media records in the storage system (currently using file system storage,
     *     but may include database storage in the future).
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "message" : "The files have been uploaded successfully."
     *          }
     *     }</pre>
     * </p>
     *
     * @param files A {@link List} of {@link MultipartFile} containing the media files. Must not be null or empty.
     * @return A {@link ResponseEntity} containing a confirmation message upon successful upload.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#BAD_REQUEST}
     *                                  if the given <b>files</b> is invalid.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}
     *                                  if the save operation fails.
     * */
    @PostMapping
    public ResponseEntity<StringResponse> addMedia(
            @RequestParam List<MultipartFile> files
    ) {
        log.debug("Adding new files...");
        if (files == null || files.isEmpty()) {
            log.warn("Provided file list is invalid. 'files={}'", files);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided file list is invalid.");
        }

        try {
            mediaService.saveAll(files);
        } catch (IOException e) {
            log.error("File upload operation failed. 'message={}'", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File upload operation failed.");
        }

        return ResponseEntity.ok(new StringResponse("The files have been uploaded successfully."));
    }


    /**
     * Deletes a media file from the system.
     *
     * <p>
     *     Removes the media record and deletes the associated file from storage.
     *     This operation is irreversible.
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "message" : "The media file has been successfully deleted."
     *          }
     *     }</pre>
     * </p>
     *
     * @param fileName The name of the media file to delete. Must not be null.
     * @return A {@link ResponseEntity} containing a confirmation message upon successful deletion.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#NOT_FOUND}
     *                                  if the given <b>fileName</b> does not correspond to any media file in the system.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}
     *                                  if delete operation fails.
     * */
    @DeleteMapping("/{fileName}")
    public ResponseEntity<StringResponse> deleteMedia(
            @PathVariable String fileName
    ) {
        log.debug("Deleting the file...");
        Media media = mediaService.findMediaByName(fileName) ///< Handles file existence check
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File '%s' doesn't exist".formatted(fileName)));

        try {
            mediaService.delete(media);
        } catch (IOException e) {
            log.error("File deletion operation failed. 'message={}'", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File deletion operation failed.");
        }

        return ResponseEntity.ok(new StringResponse("The media file has been successfully deleted."));
    }
}
