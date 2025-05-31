package pl.magzik.my_hub.dto.studies;

import java.time.LocalDateTime;

public record StudyFileDto(

    Integer id,
    String name,
    String path,
    String type,
    LocalDateTime creationDate

) { }
