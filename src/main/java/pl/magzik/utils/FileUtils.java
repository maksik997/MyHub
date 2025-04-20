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

    /**
     * Retrieves and returns {@link Stream} of files found in the given parent directory.
     * <p>
     *     If given {@code parent} param is invalid - is {@code null}, doesn't exist,
     *     or is not a directory the exception is thrown.
     *     To be precise {@link NullPointerException} is thrown when spoken param is null,
     *     and {@link IllegalArgumentException} is thrown in both of the remaining cases.
     * </p>
     * <p>
     *     If no elements have been found in the given {@code parent} param, the empty {@link Stream} is returned.
     * </p>
     * <p>
     *     Additionally, the method applies given filter - {@link Predicate}, and a mapper - {@link Function} both given as params.
     * </p>
     *
     * @param <T> The type of the resulting {@link Stream} returned by this method. Defined by {@code function} param.
     * @param parent The parent file name. Can be relative, or absolute. Must be non-null, exist, and be a directory,
     * @param predicate The filter for the {@code parent} file child elements. Must be non-null.
     * @param function The mapper which changes found child entries into {@code T} type. Must be non-null.
     *
     * @return The {@link Stream} of filtered and mapped child entries of the {@code parent} directory.
     *
     * @throws NullPointerException If any of the params is null.
     * @throws IllegalArgumentException If given {@code parent} param is not a file reference - not exists, or is not a directory.
     * */
    public static <T> Stream<T> getFilesInDirectory(String parent, Predicate<File> predicate, Function<File, T> function) {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(function);

        // Check given parent file reference.
        File parentFile = new File(parent);
        if (!parentFile.exists() || !parentFile.isDirectory()) {
            log.warn("Provided parent file '{}' doesn't exists, or is not a directory.", parent);
            throw new IllegalArgumentException("Invalid parent file.");
        }
        parentFile = parentFile.getAbsoluteFile(); // To be precise, we want to use absolute file references.

        // Get and validate all files in parent file reference.
        File[] files = parentFile.listFiles();
        if (files == null || files.length == 0) { // Null check is redundant as we check it earlier - according to the docs at least.
                                                  // But IDE don't like it when we don't check so...
            log.warn("Provided parent file '{}' is empty.", parent);
            return Stream.empty();
        }

        return Arrays.stream(files)
                .filter(predicate) // Filter using provided Predicate
                .map(function); // Map using given Function
    }
}
