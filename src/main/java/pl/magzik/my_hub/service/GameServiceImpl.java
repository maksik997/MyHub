package pl.magzik.my_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    @Override
    public List<GameDTO> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(GameDTO::of)
                .toList();
    }

    @Override
    public GameDTO findByName(String name) {
        Objects.requireNonNull(name);
        var g = gameRepository.findByName(name);
        return GameDTO.of(g);
    }

    @Deprecated
    @Override
    public void addByName(MultipartFile file) {
        Objects.requireNonNull(file);
        gameRepository.save(file);
    }

    @Deprecated
    @Override
    public void deleteByName(String fileName) {
        Objects.requireNonNull(fileName);
        Game game = getOrThrow(fileName);
        gameRepository.delete(game);
    }

    private Game getOrThrow(String name) {
        Objects.requireNonNull(name);

        return gameRepository.findByName(name);
    }

}

