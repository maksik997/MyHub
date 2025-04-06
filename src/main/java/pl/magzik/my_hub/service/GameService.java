package pl.magzik.my_hub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class providing interface for {@link GameRepository} class.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @see GameRepository
 * */
@Service
public class GameService {

    /* TODO:
    *   No.1 CRUD operations
    *           Another step into RESTful API.
    * */

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameDTO> findAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(g -> new GameDTO(g.name(), g.htmlFile()))
                .toList();
    }

    public Optional<Game> findGameByName(String name) {
        Objects.requireNonNull(name);
        return gameRepository.findByName(name);
    }

    public void saveGame(MultipartFile file) {
        try {
            gameRepository.save(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void deleteGame(String fileName) {
        Objects.requireNonNull(fileName);
        Game game = gameRepository.findByName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("Provided game doesn't exists."));

        try {
            gameRepository.delete(game);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
