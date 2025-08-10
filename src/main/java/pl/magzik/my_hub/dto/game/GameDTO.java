package pl.magzik.my_hub.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object used for handling data transfer outside the system.
 *
 * @author Maksymilian Strzelczak
 * @version 1.2
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
    
}
