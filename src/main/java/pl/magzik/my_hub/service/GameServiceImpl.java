package pl.magzik.my_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.exception.game.GameNotFoundException;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;
import pl.magzik.my_hub.repository.GameRepositoryImpl;

import java.util.List;
import java.util.Objects;

/**
 * todo; ...
 *
 * @since 1.0
 *
 * @author Maksymilian Strzelczak
 * @version 2.0
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository repository;
    private final GameRepositoryImpl storageRepository;

    @Override
    public List<GameDTO> findAll() {
        return repository.findAll()
                         .stream()
                         .map(GameDTO::of)
                         .toList();
    }

    @Override
    public GameDTO findByName(String name) {
        Objects.requireNonNull(name);

        var g = getOrThrow(name);
        return GameDTO.of(g);
    }

    @Deprecated
    @Override
    public void addByName(MultipartFile file) {
        // todo;
        Objects.requireNonNull(file);
        storageRepository.save(file);
    }

    @Deprecated
    @Override
    public void deleteByName(String fileName) {
        // todo;
        Objects.requireNonNull(fileName);
        Game game = getOrThrow(fileName);
//        storageRepository.delete(game);
    }

    private Game getOrThrow(String name) {
        Objects.requireNonNull(name);

        return repository.findByName(name)
                .orElseThrow(GameNotFoundException::new);
    }

}

