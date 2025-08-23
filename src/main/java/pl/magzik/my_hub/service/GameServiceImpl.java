package pl.magzik.my_hub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.game.CreateGameRequest;
import pl.magzik.my_hub.dto.game.GameDTO;
import pl.magzik.my_hub.exception.game.GameAlreadyExistsException;
import pl.magzik.my_hub.exception.game.GameInvalidFormatException;
import pl.magzik.my_hub.exception.game.GameNotFoundException;
import pl.magzik.my_hub.exception.game.GameUploadFailureException;
import pl.magzik.my_hub.mapper.GameMapper;
import pl.magzik.my_hub.model.Game;
import pl.magzik.my_hub.repository.GameRepository;
import pl.magzik.my_hub.repository.GameStorageRepository;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of GameService interface.
 *
 * @since 1.0
 *
 * @author Maksymilian Strzelczak
 * @version 2.3
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository entityRepository;
    private final GameStorageRepository storageRepository;
    private final GameMapper mapper;

    @Override
    public List<GameDTO> findAll() {
        return entityRepository.findAll()
                               .stream()
                               .map(mapper::toDto)
                               .toList();
    }

    @Override
    public Page<GameDTO> findAll(Pageable pageable) {
        return entityRepository.findAll(pageable)
                               .map(mapper::toDto);
    }

    @Override
    public GameDTO findById(long id) throws GameNotFoundException {
        var game = getOrThrow(id);
        return mapper.toDto(game);
    }

    @Override
    @Transactional
    public void add(CreateGameRequest request,
                    MultipartFile file) throws GameAlreadyExistsException,
                                               GameUploadFailureException,
                                               GameInvalidFormatException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(file);

        var game = mapper.toEntityFromRequest(request);
        var revision = storageRepository.save(file);
        game.setCurrentGameRevision(revision);
        var html = storageRepository.locateExecutable(revision);
        game.setHtml(html);

        entityRepository.save(game);
    }

    @Override
    @Transactional
    public void update(long id,
                       CreateGameRequest request,
                       MultipartFile file) throws GameNotFoundException,
                                                  GameUploadFailureException,
                                                  GameInvalidFormatException {
        Objects.requireNonNull(request);

        var game = getOrThrow(id);

        if (!Objects.equals(game.getName(), request.getName())) {
            // Update game's entity
            game.setName(request.getName());
        }
        if (file != null && !file.isEmpty()) {
            // Update the game's source files.
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
    public void delete(long id) throws GameNotFoundException {
        var game = getOrThrow(id);
        var revId = game.getCurrentGameRevision();
        storageRepository.delete(revId);
        entityRepository.delete(game);
    }

    @Override
    public String locate(long id) throws GameNotFoundException {
        var game = getOrThrow(id);
        return game.getUniformLocator();
    }

    /**
     * Retrieves a game entity by the provided id. If game entity cannot be found throws exception.
     *
     * @param id an identifier for looked up game.
     * @return game entity that corresponds to the provided identifier.
     * @throws GameNotFoundException if game entity cannot be found.
     * */
    private Game getOrThrow(long id) throws GameNotFoundException {
        return entityRepository.findById(id)
                .orElseThrow(GameNotFoundException::new);
    }

}

