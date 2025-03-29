package pl.magzik.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.magzik.service.MediaService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;

/**
 * REST Controller responsible for handling media-related operations.
 * <p>
 *     Provides endpoints for managing media files, including retrieval, uploading, updating, and deletion.
 *     Ensures a structured and secure data exchange through {@link MediaDTO}.
 * </p>
 *
 * @since 1.2
 * @author Maksymilian Strzelczak
 * */
@RestController
@RequestMapping("/api/1.2/media")
public class MediaRestController {

    /**
     * Retrieves all media records stored in the system.
     * <p>
     *      This method fetches media records from {@link MediaService}
     *      and returns them as a list of {@link MediaDTO},
     *      ensuring a safe and structured data representation for the user.
     * </p>
     *
     * @return A {@link ResponseEntity} containing a list of all available media wrapped in {@link MediaDTO}.
     * */
    @GetMapping
    public ResponseEntity<?> getAllMedia() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Retrieves and serves a media file as a downloadable resource.
     * <p>
     *     Searches for a media file associated with the given file name in {@link MediaService}.
     *     If found, returns the file as a {@link Resource} stream with appropriate headers,
     *     allowing the client to download or display it.
     * </p>
     *
     * @param fileName The name of the media file to retrieve.
     * @return A {@link ResponseEntity} containing a {@link Resource} with the media file data.
     * @throws ResponseStatusException If the requested file does not exist.
     * */
    @GetMapping("/{fileName}")
    public ResponseEntity<?> getMedia(String fileName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Uploads a new media file and registers it in the system.\
     *
     * <p>
     *     Processes the uploaded media file, stores it securely,
     *     and creates a corresponding media record in the extent (currently referring to file system storage,
     *     but may include database storage in the future)..
     *     The request must include a valid {@link MediaDTO} containing the file resource.
     * </p>
     *
     * @param mediaDTO A {@link MediaDTO} containing the media file and its metadata.
     * @return A {@link ResponseEntity} containing the newly created media record wrapped in a {@link MediaDTO}.
     * @throws ResponseStatusException If the upload fails due to an invalid request or storage error.
     * */
    @PostMapping
    public ResponseEntity<?> addMedia() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Updates the metadata of an existing media file.
     *
     * <p>
     *     This method allows renaming the specified media file while preserving its content.
     *     Currently, only the file name update is supported.
     * </p>
     *
     * @param fileName The current name of the media file to be updated.
     * @param mediaDTO A {@link MediaDTO} containing the updated metadata (e.g., new file name).
     * @return A {@link ResponseEntity} containing updated media record wrapped in {@link MediaDTO}.
     * @throws ResponseStatusException If no media file exists with the given name,
     *                                  or if the update operation fails.
     * */
    @PutMapping("/{fileName}")
    public ResponseEntity<?> updateMedia(String fileName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Deletes a media file from the system.
     *
     * <p>
     *     Removes the media record and deletes the associated file from storage.
     *     This operation is irreversible.
     * </p>
     *
     * @param fileName The name of the media file to delete.
     * @return A {@link ResponseEntity} confirming the successful deletion.
     * @throws ResponseStatusException If no media file exists with the given name,
     *                                  or the deletion operation fails.
     * */
    @DeleteMapping("/{fileName}")
    public ResponseEntity<?> deleteMedia(String fileName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
