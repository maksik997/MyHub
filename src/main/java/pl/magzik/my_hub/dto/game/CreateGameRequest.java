package pl.magzik.my_hub.dto.game;

import lombok.Data;

/**
 * Data Transfer Object for game creation.
 *
 * @since 1.3
 * @version 1.1
 * @author Maksymilian Strzelczak
 * */
@Data
public class CreateGameRequest {

    private String name;

}
