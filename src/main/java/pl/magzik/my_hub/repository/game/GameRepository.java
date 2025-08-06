package pl.magzik.my_hub.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.magzik.my_hub.model.Game;

import java.util.Optional;

/**
 * Classic JPA repository.
 *
 * @since 1.3
 *
 * @author Maksymilian Strzelczak
 * */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByName(String name);

}
