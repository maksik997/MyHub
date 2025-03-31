package pl.magzik.dto;

import pl.magzik.model.Media;

/**
 * Data Transfer Object used for handling data transfer outside the system.
 * MediaDTO - for getting all data from Media.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 */
public record MediaDTO(String fileName, Media.MediaType mediaType) {
}
