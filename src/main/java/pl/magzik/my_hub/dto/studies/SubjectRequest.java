package pl.magzik.my_hub.dto.studies;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectRequest(

    @NotBlank
    @Size(min = 2, max = 4)
    String code,

    @NotBlank
    @Size(min = 3, max = 100)
    String name,

    @Size(max = 500)
    String description

) { }
