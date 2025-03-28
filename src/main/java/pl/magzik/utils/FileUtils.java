package pl.magzik.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
}
