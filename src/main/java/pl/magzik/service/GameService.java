package pl.magzik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.magzik.model.Game;
import pl.magzik.repository.GameRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> findGameById(UUID id) {
        Objects.requireNonNull(id);
        return gameRepository.findById(id);
    }

    // TODO No.1:
    //  Add Create, Update, Delete operation support.

}
