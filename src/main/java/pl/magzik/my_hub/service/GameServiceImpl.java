package pl.magzik.my_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.CreateGameRequest;
import pl.magzik.my_hub.dto.GameDTO;
import pl.magzik.my_hub.exception.game.GameNotFoundException;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;
import pl.magzik.my_hub.repository.GameStorageRepository;

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

    private final GameRepository entityRepository;
    private final GameStorageRepository storageRepository;
//    private final GameRepositoryImpl storageRepository;

    @Override
    public List<GameDTO> findAll() {
        return entityRepository.findAll()
                               .stream()
                               .map(GameDTO::of)
                               .toList();
    }

    @Override
    public Page<GameDTO> findAll(Pageable pageable) {
        return entityRepository.findAll(pageable)
                               .map(GameDTO::of);
    }

    @Override
    public GameDTO findById(long id) {
        var game = getOrThrow(id);
        return GameDTO.of(game);
    }

    @Override
    public GameDTO findByName(String name) {
        Objects.requireNonNull(name);

        var game = entityRepository.findByName(name)
                .orElseThrow(GameNotFoundException::new);
        return GameDTO.of(game);
    }

    @Override
    @Transactional
    public void add(CreateGameRequest request, MultipartFile file) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(file);

        var game = CreateGameRequest.toEntity(request);
        var revision = storageRepository.save(file);
        game.setCurrentGameRevision(revision);
        var html = storageRepository.locateExecutable(revision);
        game.setHtml(html);

        entityRepository.save(game);
    }

    @Deprecated
    public void addByName(MultipartFile file) { // Legacy
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public void update(long id, CreateGameRequest request, MultipartFile file) {
        Objects.requireNonNull(request);

        var game = getOrThrow(id);

        if (!game.getName().equals(request.getName())) {
            game.setName(request.getName());
        }
        if (file != null) {
            var oldRev = game.getCurrentGameRevision();
            var newRev = storageRepository.update(oldRev, file);
            game.setCurrentGameRevision(newRev);
            var html = storageRepository.locateExecutable(newRev);
            game.setHtml(html);
        }

        entityRepository.save(game);
    }

    @Override
    @Transactional
    public void delete(long id) {
        var game = getOrThrow(id);
        var revId = game.getCurrentGameRevision();
        storageRepository.delete(revId);
        entityRepository.delete(game);
    }

    @Override
    public String locate(long id) {
        var game = getOrThrow(id);
        var revId = game.getCurrentGameRevision();
        var html = game.getHtml();
        return "%s/%s".formatted(revId, html);
    }

    @Deprecated
    public void deleteByName(String fileName) { // Legacy
        throw new UnsupportedOperationException();
    }

    private Game getOrThrow(long id) {
        return entityRepository.findById(id)
                .orElseThrow(GameNotFoundException::new);
    }

}

