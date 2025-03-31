package pl.magzik.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.magzik.dto.*;
import pl.magzik.model.Media;
import pl.magzik.service.MediaService;

import java.io.File;
import java.util.List;

/**
 * REST Controller responsible for handling media-related operations.
 * <p>
 *     Provides endpoints for managing media files, including retrieval, uploading, updating, and deletion.
 *     Ensures a structured and secure data exchange through different Data Transfer Object of the {@link pl.magzik.model.Media} record.
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
        Media media = mediaService.findMediaByName(fileName) ///< Handles file existence check
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "File '%s doesn't exists".formatted(fileName)));
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
     *     The request must include a valid {@link BulkMediaUploadDTO} containing the file resources.
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "content" : [...List<MultipartFile>...]
     *          }
     *     }</pre>
     * </p>
     *
     * @param bulkMediaUploadDTO A {@link BulkMediaUploadDTO} containing the media files and their metadata. Must not be null.
     * @return A {@link ResponseEntity} containing a list of {@link MultipartFile} representing the newly created media records.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#BAD_REQUEST}
     *                                  if the given <b>bulkMediaUploadDTO</b> is invalid.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}
     *                                  if the save operation fails.
     * */
    @PostMapping
    public ResponseEntity<List<MediaDTO>> addMedia(
            @RequestBody BulkMediaUploadDTO bulkMediaUploadDTO
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Updates the metadata of an existing media file.
     *
     * <p>
     *     This method allows renaming the specified media file while preserving its content.
     *     Currently, only file name updates are supported.
     * </p>
     * <p>
     *     Example response format:
     *     <pre>{@code
     *          {
     *              "content" : [...MediaDTO...]
     *          }
     *     }</pre>
     * </p>
     *
     * @param fileName The current name of the media file to update. Must not be null.
     * @param mediaUpdateDTO A {@link MediaUpdateDTO} containing the updated metadata (e.g., new file name).
     * @return A {@link ResponseEntity} containing updated media record wrapped in {@link MediaDTO}.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#NOT_FOUND}
     *                                  if the given <b>fileName</b> does not correspond to any media file in the system.
     * @throws ResponseStatusException with {@link org.springframework.http.HttpStatus#INTERNAL_SERVER_ERROR}
     *                                  if the update operation fails.
     * */
    @PutMapping("/{fileName}")
    public ResponseEntity<MediaDTO> updateMedia(
            @PathVariable String fileName,
            @RequestBody MediaUpdateDTO mediaUpdateDTO
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
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
    public ResponseEntity<String> deleteMedia(
            @PathVariable String fileName
    ) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
