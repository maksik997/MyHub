package pl.magzik.my_hub.dto.game;

import lombok.Data;
import pl.magzik.my_hub.model.Game;

import java.util.Objects;

/**
 * Data Transfer Object for game creation.
 *
 * @since 1.3
 * @version 1.0
 * @author Maksymilian Strzelczak
 * */
@Data
public class CreateGameRequest {

    private String name;

    /**
     * Create an entity from the provided DTO.
     *
     * @param dto a dto used in entity construction. Must not be null.
     * @return a newly created entity.
     * */
    public static Game toEntity(CreateGameRequest dto) {
        Objects.requireNonNull(dto);
        var game = new Game();
        game.setName(dto.getName());
        return game;
    }

}
