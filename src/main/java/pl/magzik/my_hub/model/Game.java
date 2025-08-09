package pl.magzik.my_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A game entity.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 *
 * @since 1.3
 * */
@Entity
@Data
public class Game {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Basic(optional = false)
    @Column(nullable = false,
            length = 200)
    private String name;

    @Basic(optional = false)
    @Column(nullable = false,
            length = 1000)
    private String html;

    @Basic(optional = false)
    @Column(nullable = false)
    private UUID currentGameRevision;

    @Basic(optional = false)
    @Column(nullable = false,
            updatable = false)
    private LocalDateTime creationDate;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime modificationDate;

    @Transient
    public String getUniformLocator() {
        return "%s/%s".formatted(currentGameRevision, html);
    }

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
        preUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationDate = LocalDateTime.now();
    }

}
