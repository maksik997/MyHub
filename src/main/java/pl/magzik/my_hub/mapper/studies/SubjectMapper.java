package pl.magzik.my_hub.mapper.studies;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.magzik.my_hub.dto.studies.SubjectDto;
import pl.magzik.my_hub.dto.studies.SubjectRequest;
import pl.magzik.my_hub.model.studies.Subject;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    @Named("toDtoBasic")
    @Mapping(target = "fileCount", expression = "java(subject.getFiles() != null ? subject.getFiles().size() : 0)")
    @Mapping(target = "files", ignore = true)
    SubjectDto toDtoBasic(Subject subject);

    @Mapping(target = "fileCount", expression = "java(subject.getFiles() != null ? subject.getFiles().size() : 0)")
    SubjectDto toDtoWithFiles(Subject subject);

    @IterableMapping(qualifiedByName = "toDtoBasic")
    List<SubjectDto> toDtoListBasic(List<Subject> subjects);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "files", ignore = true)
    Subject toEntity(SubjectRequest request);

}
