package pl.magzik.utils;

import net.lingala.zip4j.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class for the file management.
 *
 * @author Maksymilian Strzelczak
 * @version 1.1
 * */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static <T> Stream<T> getFileStream(String parent, Predicate<File> predicate, Function<File, T> function) {
        Objects.requireNonNull(parent);

        File parentFile = new File(parent);
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            log.warn("Provided parent file '{}' doesn't exists, or is not a directory.", parent);
            throw new IllegalArgumentException("Invalid parent file.");
        }

        File[] files = parentFile.listFiles();
        if (files == null || files.length == 0) {
            log.warn("Provided parent file '{}' is empty.", parent);
            return Stream.empty();
        }

        return Arrays.stream(files)
                .filter(predicate)
                .map(function);
    }

    public static Path unzipArchive(Path archive, Path destination, String requiredExtension) throws IOException {
        // I don't like this method.
        if (!Files.exists(destination) || Files.isRegularFile(destination)) {
            throw new IllegalArgumentException("Given destination directory doesn't exist or is not a directory.");
        }
        if (!Files.exists(archive) || !Files.isRegularFile(archive)) {
            throw new IllegalArgumentException("Given zip archive doesn't exist, or is not a regular file.");
        }

        try (ZipFile zipFile = new ZipFile(archive.toFile())) {
            zipFile.extractAll(destination.toString());
        }

        return Path.of(archive.getFileName().toString()
                .split("\\.")[0]
                .replaceAll("_", " ")
        );
    }

}
