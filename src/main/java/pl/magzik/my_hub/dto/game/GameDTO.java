package pl.magzik.my_hub.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.magzik.my_hub.model.Game;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Data Transfer Object used for handling data transfer outside the system.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @since 1.2
 * */
@AllArgsConstructor
@Data
public class GameDTO {
    private long id;
    private String name;
    private String html;
    private UUID currentGameRevision;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    /**
     * Create a DTO from provided entity.
     *
     * @param game a game used in dto construction. Must not be null.
     * @return a newly create DTO.
     * */
    public static GameDTO toDto(Game game) {
        Objects.requireNonNull(game);
        return new GameDTO(game.getId(),
                           game.getName(),
                           game.getHtml(),
                           game.getCurrentGameRevision(),
                           game.getCreationDate(),
                           game.getModificationDate());
    }
    
}
