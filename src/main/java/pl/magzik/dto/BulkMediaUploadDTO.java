package pl.magzik.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data Transfer Object used for handling bulk media file uploads to the system.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record BulkMediaUploadDTO(List<MultipartFile> files) {
}
