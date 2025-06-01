package pl.magzik.my_hub.model.studies;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String code;
    private String name;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<StudyFile> files;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
        this.modificationDate = this.creationDate;
    }

    @PreUpdate
    public void preUpdate() {
        this.modificationDate = LocalDateTime.now();
    }

}
