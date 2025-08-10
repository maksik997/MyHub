package pl.magzik.my_hub.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.magzik.my_hub.dto.game.CreateGameRequest;
import pl.magzik.my_hub.dto.game.GameDTO;
import pl.magzik.my_hub.model.Game;

/**
 * Game related mapper interface
 *
 * @version 1.0
 * @author Maksymilian Strzelczak
 *
 * @since 1.3
 * */
@Mapper(componentModel = "spring")
public interface GameMapper {

    GameDTO toDto(Game game);

    Game toEntity(GameDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "html", ignore = true)
    @Mapping(target = "currentGameRevision", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    Game toEntityFromRequest(CreateGameRequest request);

    CreateGameRequest toRequest(Game game);

}
