package pl.magzik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Game;
import pl.magzik.repository.GameRepository;

import java.io.IOException;
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

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> findGameByName(String name) {
        Objects.requireNonNull(name);
        return gameRepository.findByName(name);
    }

    public Game saveGame(MultipartFile file) throws IOException {
        return gameRepository.save(file);
    }

    public void deleteGame(String fileName) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
