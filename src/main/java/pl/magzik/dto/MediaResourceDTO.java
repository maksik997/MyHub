package pl.magzik.dto;

import org.springframework.core.io.Resource;

/**
 * Data Transfer Object used for handling media file download.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record MediaResourceDTO(Resource resource) {
}
