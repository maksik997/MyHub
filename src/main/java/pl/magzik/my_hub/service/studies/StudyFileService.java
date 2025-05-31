package pl.magzik.my_hub.service.studies;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.magzik.my_hub.mapper.studies.StudyFileMapper;
import pl.magzik.my_hub.repository.StudyFileRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyFileService {

    private final StudyFileRepository studyFileRepository;
    private final StudyFileMapper studyFileMapper;

}
