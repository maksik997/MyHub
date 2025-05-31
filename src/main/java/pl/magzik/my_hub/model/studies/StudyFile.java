package pl.magzik.my_hub.model.studies;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class StudyFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String path;
    private String type;
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

}
