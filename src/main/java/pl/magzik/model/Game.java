package pl.magzik.model;

//import org.jetbrains.annotations.Contract;
//import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

/**
 * Represents a game directory containing an HTML file, typically used to identify a game
 * and provide access to the HTML file associated with that game.
 * <p>
 * This record holds the name of the game, the name of the `.html` file within the directory,
 * and the full path to the `.html` file.
 *
 * <p>The {@link Game} record is immutable and designed to be used with files on a file system.</p>
 *
 * <p>Instances of {@link Game} are created via the static {@link #of(File)} method,
 * which requires a directory containing exactly one `.html` file, along with a base directory
 * for constructing the full path to the `.html` file.</p>
 *
 * @param name the name of the game directory (typically the directory name).
 * @param htmlFile the name of the `.html` file within the game directory.
 */
public record Game(UUID id, String name, String htmlFile) {

    /**
     * Creates a {@link Game} instance from the given directory containing a `.html` file.
     * The method searches for the first `.html` file in the directory and constructs a {@link Game}
     * object using the directory name and the `.html` file's name as properties.
     *
     * @param directory the directory to scan for the `.html` file (must not be {@code null}).
     * @return a new {@link Game} object containing the directory name, `.html` file name, and full path to the file.
     * @throws IllegalArgumentException if the directory is unreadable, or if no `.html` file is found in the directory.
     * */
    /*@NotNull
    @Contract("_ -> new")*/
    public static Game of(/*@NotNull*/ File directory) {
        String name = directory.getName();

        File[] innerFiles = directory.listFiles();
        if (innerFiles == null)
            throw new IllegalArgumentException("Couldn't read this directory: " + directory.getAbsolutePath());

        File htmlFile = Arrays.stream(innerFiles)
            .filter(f -> f.getName().endsWith(".html"))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("There is no `.html` file in this directory: " + directory.getAbsolutePath()));

        return new Game(UUID.randomUUID(), name, htmlFile.getName());
    }
}
