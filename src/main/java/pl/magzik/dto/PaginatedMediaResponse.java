package pl.magzik.dto;

import java.util.List;

/**
 * Data Transfer Object used for handling paginated {@link MediaDTO} transfers outside the system.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record PaginatedMediaResponse(
        List<MediaDTO> content,
        int page,
        int size,
        int totalPages,
        long totalElements
) {}
