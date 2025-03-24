package pl.magzik.controller;

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
import pl.magzik.service.MediaService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller class that shares various endpoints regarding {@link Media} handling.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @see Media
 * @see MediaService
 */
@Controller
@RequestMapping("/media")
public class MediaController {

    /* TODO:
     *   No.1 Transition to RESTful API.
     * */

    private static final Logger log = LoggerFactory.getLogger(MediaController.class);

    private final MediaService mediaService;

    @Autowired
    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    public String getMedia(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "30") int size,
        Model model
    ) {
        if (page < 0 || size <= 0) {
            log.warn("Provided request have invalid parameters. Page: {}, Size: {}", page, size);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both page and size should have positive or zero value.");
        }

        long totalCount = mediaService.countAllMedia();
        int totalPages = (int) Math.ceil((double) totalCount / size);

        if (page >= totalPages) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found.");
        }

        model.addAttribute("media", mediaService.findAllMedia(page, size));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);

        return "media";
    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> getMediaFile(@PathVariable(name = "filename") String filename) {
        try {
            Media media = mediaService.findMediaByName(filename)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Media file '" + filename + "' not found."));

            File file = new File(media.path());
            if (!file.exists()) {
                log.warn("File not found: {}", file);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + filename);
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, media.getMimeType())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + media.fileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error retrieving file: {}", filename, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during media aggregation. Error: " + e.getMessage(), e);
        }
    }

    @PostMapping("/upload")
    public String uploadFiles(@RequestParam(name = "files") List<MultipartFile> files, Model model) {
        if (files == null || files.isEmpty()) {
            model.addAttribute("message", "No files provided for upload.");
            return "upload";
        }

        try {
            mediaService.saveAll(files);
            log.info("Successfully uploaded {} files.", files.size());
            model.addAttribute("message", "File uploaded successfully");
        } catch (IOException e) {
            log.warn("File upload failed: {}", e.getMessage(), e);
            model.addAttribute("message" ,"File upload failed: " + e.getMessage());
        }

        return "upload";
    }

    /**
     * Helper endpoint, for displaying status messages for upload requests.
     * */
    @GetMapping("/upload")
    public String uploadFilesHandler(Model model) {
        model.addAttribute("message", "");
        return "upload";
    }
}
