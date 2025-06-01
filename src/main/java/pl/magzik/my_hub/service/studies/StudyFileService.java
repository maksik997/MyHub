package pl.magzik.my_hub.service.studies;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.my_hub.dto.studies.StudyFileDto;
import pl.magzik.my_hub.dto.studies.StudyFileUploadResult;
import pl.magzik.my_hub.mapper.studies.StudyFileMapper;
import pl.magzik.my_hub.model.studies.StudyFile;
import pl.magzik.my_hub.model.studies.Subject;
import pl.magzik.my_hub.repository.StudyFileRepository;
import pl.magzik.my_hub.repository.SubjectRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * TODO: Docs...
 *
 * @author Maksymilian Strzelczak
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudyFileService {

    private final SubjectRepository subjectRepository;
    private final StudyFileRepository studyFileRepository;
    private final StudyFileMapper studyFileMapper;

    @Value("${my-hub.studies-directory}")
    private String studyFileUploadDirectory;

    public List<StudyFileDto> findAllSubjectsStudyFiles(Integer subjectId) {
        Objects.requireNonNull(subjectId);

        log.debug("Find all subject's {} study files has been called", subjectId);

        return subjectRepository
                .findById(subjectId)
                .map(Subject::getFiles)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found."))
                .stream()
                .map(studyFileMapper::toDto)
                .toList();
    }

    @SneakyThrows
    public Resource openFile(Integer id) {
        Objects.requireNonNull(id);

        log.debug("Open study file {} has been called.", id);

        var studyFile = studyFileRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Study file not found."));

        Path filePath = Path
                .of(studyFileUploadDirectory)
                .resolve(studyFile.getPath())
                .normalize();

        if (!filePath.startsWith(studyFileUploadDirectory)) {
            log.warn("Unauthorized access try has been detected to '{}'.", filePath);
            throw new SecurityException("Unauthorized access try has been detected.");
        }
        if (!Files.exists(filePath)) {
            log.warn("File '{}' not found.", filePath);
            throw new FileNotFoundException("File not found."); /// <- Sneaky throw
        }

        return new UrlResource(filePath.toUri()); /// <- Sneaky throw
                                                  ///  if this happens = 500 Internal Server Error
    }

    @SneakyThrows
    public List<StudyFileUploadResult> saveStudyFiles(Integer subjectId, List<MultipartFile> files) {
        Objects.requireNonNull(subjectId);
        Objects.requireNonNull(files);

        log.debug("Save {} files for subject {} has been called.", files.size(), subjectId);

        var subject = subjectRepository
                .findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found."));

        Path subjectDirectory = Path
                .of(studyFileUploadDirectory)
                .resolve(subjectId.toString());
        if (!Files.exists(subjectDirectory)) {
            Files.createDirectories(subjectDirectory); /// <- Sneaky throw
                                                       ///  Should result in 500 Internal Server Error.
        }

        List<StudyFileUploadResult> results = files
                .stream()
                .map(f -> saveStudyFile(subjectDirectory, f))
                .toList();

        results.stream()
                .filter(StudyFileUploadResult::success)
                .map(StudyFileUploadResult::fileName)
                .map(fn -> createStudyFile(fn, subjectDirectory))
                .forEach(studyFileRepository::save);

        return results;
    }

    public void deleteStudyFile(Integer id) {
        Objects.requireNonNull(id);

        log.debug("Delete study file {} has been called.", id);

        var studyFile = studyFileRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Study file not found."));

        studyFileRepository.delete(studyFile);
    }

    private StudyFileUploadResult saveStudyFile(Path subjectDirectory, MultipartFile file) {
        Objects.requireNonNull(subjectDirectory);
        Objects.requireNonNull(file);

        // Given file validation
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) { // Gracefully
            log.warn("Provided file has not been defined, or is unavailable.");
            return new StudyFileUploadResult(
                    "undefined",
                    false,
                    "File is not defined, or unavailable"
            );
        }

        // Destination path construction & validation
        Path destinationPath = subjectDirectory.resolve(originalFileName);
        if (Files.exists(destinationPath)) { // Gracefully
            log.warn("File '{}' already exists.", destinationPath.getFileName());
            return new StudyFileUploadResult(
                    destinationPath.getFileName().toString(),
                    false,
                    "File already exists"
            );
        }

        // File upload
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destinationPath);
        } catch (IOException e) {
            log.warn("Unexpected I/O error has occurred while saving to file {}.", destinationPath.getFileName());
            return new StudyFileUploadResult(
                    destinationPath.getFileName().toString(),
                    false,
                    "Unexpected I/O error occurred"
            );
        }

        log.debug("File: name='{}' has been successfully uploaded", destinationPath.getFileName());

        return new StudyFileUploadResult(
                destinationPath.getFileName().toString(),
                true,
                "Successfully uploaded"
        );
    }

    @SneakyThrows
    private StudyFile createStudyFile(String fileName, Path subjectDirectory) {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(subjectDirectory);

        Path uploadDirectory = Path.of(studyFileUploadDirectory);
        Path filePath = subjectDirectory.resolve(fileName);

        StudyFile studyFile = new StudyFile();
        studyFile.setName(fileName);
        studyFile.setPath(uploadDirectory.relativize(filePath).toString());
        studyFile.setType(Files.probeContentType(filePath)); /// <- Sneaky throw
        return studyFile;
    }

}
