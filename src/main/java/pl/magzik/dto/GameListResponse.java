package pl.magzik.dto;

import java.util.List;

/**
 * Data Transfer Object used for handling {@link GameDTO} collection transfers outside the system.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record GameListResponse(List<GameDTO> content) {
}
