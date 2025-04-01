package pl.magzik.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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
 * @since 1.1
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

    /**
     * Extracts the given {@code zip} archive to the provided destination directory.
     *
     * @param archive A {@link Path} to the {@code zip} archive. Must not be null.
     * @param destination A {@link Path} to the destination directory. Must not be null.
     * @throws IOException if archive extraction fails.
     * */
    public static void unzipArchive(Path archive, Path destination) throws IOException {
        Objects.requireNonNull(archive);
        Objects.requireNonNull(destination);
        if (!Files.exists(destination) || !Files.isDirectory(destination)) {
            throw new IllegalArgumentException("Provided destination directory does not exist or is not a directory.");
        }
        if (!Files.exists(archive) || !Files.isRegularFile(archive)) {
            throw new IllegalArgumentException("Provided archive doesn't exists or is not a file.");
        }

        try (ZipFile zipFile = new ZipFile(archive.toFile())) {
            zipFile.extractAll(destination.toString());
        }
    }

}
