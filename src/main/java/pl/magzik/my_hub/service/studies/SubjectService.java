package pl.magzik.my_hub.service.studies;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.magzik.my_hub.dto.studies.SubjectDto;
import pl.magzik.my_hub.dto.studies.SubjectRequest;
import pl.magzik.my_hub.mapper.studies.SubjectMapper;
import pl.magzik.my_hub.repository.SubjectRepository;

import java.util.Objects;

/**
 * TODO: Docs
 * @author Maksymilian Strzelczak
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public Page<SubjectDto> findAllSubjects(Pageable pageable) {
        Objects.requireNonNull(pageable);

        log.debug("Find all subjects has been called.");

        return subjectRepository
                .findAll(pageable)
                .map(subjectMapper::toDtoBasic);
    }

    public SubjectDto findSubjectById(Integer id) {
        Objects.requireNonNull(id);

        log.debug("Find subject by id {} has been called.", id);

        return subjectRepository
                .findById(id)
                .map(subjectMapper::toDtoWithFiles)
                .orElseThrow(() -> handleSubjectNotFound(id));
    }

    public SubjectDto saveSubject(SubjectRequest subjectRequest) {
        Objects.requireNonNull(subjectRequest);

        log.debug("Save new subject has been called.");

        var subject = subjectMapper.toEntity(subjectRequest);
        subject = subjectRepository.save(subject);
        return subjectMapper.toDtoWithFiles(subject);
    }

    public SubjectDto updateSubject(Integer id, SubjectRequest subjectRequest) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(subjectRequest);

        log.debug("Update subject {} has been called", id);

        var existing = subjectRepository
                .findById(id)
                .orElseThrow(() -> handleSubjectNotFound(id));
        subjectMapper.updateEntity(subjectRequest, existing);
        existing = subjectRepository.save(existing);
        return subjectMapper.toDtoWithFiles(existing);
    }

    public void deleteSubject(Integer id) {
        Objects.requireNonNull(id);

        log.debug("Delete subject {} has been called.", id);

        var subject = subjectRepository.findById(id)
                .orElseThrow(() -> handleSubjectNotFound(id));
        subjectRepository.delete(subject);
    }

    private EntityNotFoundException handleSubjectNotFound(Integer id) {
        Objects.requireNonNull(id);

        log.warn("Subject {} couldn't be found", id);
        return new EntityNotFoundException("Subject %s couldn't be found.".formatted(id));
    }

}
