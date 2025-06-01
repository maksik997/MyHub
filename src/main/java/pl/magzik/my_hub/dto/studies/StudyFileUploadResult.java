package pl.magzik.my_hub.dto.studies;

public record StudyFileUploadResult(

    String fileName,
    boolean success,
    String errorMessage // <- nullable

) { }
