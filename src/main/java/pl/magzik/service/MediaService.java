package pl.magzik.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.magzik.model.Media;
import pl.magzik.repository.MediaRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class providing interface for {@link MediaRepository} class.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 * @see MediaRepository
 * */
@Service
public class MediaService {

    private final MediaRepository mediaRepository;

    @Autowired
    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Optional<Media> findMediaByName(String name) {
        Objects.requireNonNull(name);
        return mediaRepository.findByName(name);
    }

    public List<Media> findAllMedia(int page, int n) {
        return mediaRepository.findAll().stream()
                .skip((long) page*n)
                .limit(n)
                .toList();
    }

    public long countAllMedia() {
        return mediaRepository.countAll();
    }

    public void saveAll(List<MultipartFile> files) throws IOException {
        Objects.requireNonNull(files);
        mediaRepository.saveAll(files);
    }
}
