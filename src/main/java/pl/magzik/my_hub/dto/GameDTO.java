package pl.magzik.my_hub.dto;

import pl.magzik.my_hub.model.Game;

/**
 * Data Transfer Object used for handling data transfer outside the system.
 *
 * @author Maksymilian Strzelczak
 * @since 1.2
 * */
public record GameDTO(String name, String htmlFile) {
    public static GameDTO of(Game game) {
        return new GameDTO(game.name(), game.htmlFile());
    }
}
