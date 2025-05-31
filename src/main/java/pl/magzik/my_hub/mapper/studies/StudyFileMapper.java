package pl.magzik.my_hub.mapper.studies;

import org.mapstruct.Mapper;
import pl.magzik.my_hub.dto.studies.StudyFileDto;
import pl.magzik.my_hub.model.studies.StudyFile;

@Mapper(componentModel = "spring")
public interface StudyFileMapper {

    StudyFileDto toDto(StudyFile studyFile);

    StudyFile toEntity(StudyFileDto studyFileDto);

}
