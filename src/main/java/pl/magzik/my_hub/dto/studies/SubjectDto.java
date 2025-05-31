package pl.magzik.my_hub.dto.studies;

import java.time.LocalDateTime;
import java.util.List;

public record SubjectDto(

    Integer id,
    String code,
    String name,
    String description,
    LocalDateTime creationDate,
    LocalDateTime modificationDate,
    int fileCount,
    List<StudyFileDto> files

) { }
