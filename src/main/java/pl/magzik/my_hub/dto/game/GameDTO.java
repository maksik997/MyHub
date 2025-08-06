package pl.magzik.my_hub.dto.game;


import pl.magzik.my_hub.model.Game;

import java.time.LocalDateTime;
import java.util.UUID;

/* todo; -> mapstruct? */
/**
 * Data Transfer Object used for handling data transfer outside the system.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record GameDTO(
        long id,
        String name,
        String html,
        UUID currentGameRevision,
        LocalDateTime creationDate,
        LocalDateTime modificationDate
) {
    public static GameDTO of(Game game) {
        return new GameDTO(game.getId(),
                           game.getName(),
                           game.getHtml(),
                           game.getCurrentGameRevision(),
                           game.getCreationDate(),
                           game.getModificationDate());
    }
}
