package pl.magzik.my_hub.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service layer that acts as an interface to the {@link GameRepository}.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @see GameRepository
 * */
@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        log.info("Game service has been initialized.");
    }

    public List<GameDTO> findAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(GameDTO::of)
                .toList();
    }

    public Optional<Game> findGameByName(String name) {
        Objects.requireNonNull(name);
        return gameRepository.findByName(name);
    }

    public void saveGame(MultipartFile file) {
        Objects.requireNonNull(file);
        gameRepository.save(file);
    }

    public void deleteGame(String fileName) {
        Objects.requireNonNull(fileName);
        Game game = gameRepository.findByName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("Provided game doesn't exists."));
        gameRepository.delete(game);
    }
}
