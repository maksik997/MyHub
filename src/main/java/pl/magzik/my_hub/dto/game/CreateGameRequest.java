package pl.magzik.my_hub.dto.game;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for game creation.
 *
 * @since 1.3
 * @version 1.2
 * @author Maksymilian Strzelczak
 * */
@Data
public class CreateGameRequest {

    @Size(max = 200)
    private String name;

}
