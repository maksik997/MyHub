package pl.magzik.my_hub.model;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a media file, which can either be an image or a video, based on its file extension.
 * This class provides methods to determine the type of media based on the file extension
 * and stores the file name along with its determined media type ({@link MediaType#IMAGE} or {@link MediaType#VIDEO}),
 * as well as the path to the media file.
 * <p>
 * Supported image extensions: jpg, jpeg, png, gif, bmp, webp.
 * Supported video extensions: mp4, webm, ogg, avi, mov.
 *
 * @param fileName the name of the media file (e.g., "image.jpg" or "video.mp4").
 * @param type the type of the media ({@link MediaType#IMAGE} or {@link MediaType#VIDEO}).
 * @param path the path to the media file.
 *
 * @author Maksymilian Strzelczak
 * @version 1.0
 */
public record Media(String fileName, MediaType type, String path) implements Comparable<Media> {

    /**
     * Creates a {@link Media} instance based on the provided file.
     * <p>
     * This method determines the media type (either {@link MediaType#IMAGE} or {@link MediaType#VIDEO})
     * by inspecting the file's extension and returns a {@link Media} object.
     * </p>
     *
     * @param file the file to create the {@link Media} object from. The file name must have a valid extension
     *             (one of the supported image or video extensions).
     * @return a new {@link Media} object representing the file.
     * @throws IllegalArgumentException if the file extension is not supported (not an image or video).
     */
    public static Media of(File file) {
        String fileName = file.getName();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        return new Media(fileName, MediaType.of(extension), file.toString());
    }


    /**
     * Returns the MIME type for the media based on its file extension.
     * <p>
     * For images, it determines the MIME type from the file extension and
     * returns the appropriate MIME type such as "image/jpeg", "image/png", etc.
     * For videos, it returns the MIME type like "video/mp4", "video/webm", etc.
     *
     * @return the MIME type as a string for the current media file.
     * @throws IllegalArgumentException if the file extension is unsupported or invalid for the media type.
     */
    public String getMimeType() {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (type == MediaType.IMAGE) {
            return switch (extension) {
                case "jpg", "jpeg" -> "image/jpeg";
                case "png" -> "image/png";
                case "gif" -> "image/gif";
                case "bmp" -> "image/bmp";
                case "webp" -> "image/webp";
                default -> throw new IllegalArgumentException("Unsupported image type: " + extension);
            };
        } else if (type == MediaType.VIDEO) {
            return switch (extension) {
                case "mp4" -> "video/mp4";
                case "webm" -> "video/webm";
                case "ogg" -> "video/ogg";
                case "avi" -> "video/x-msvideo";
                case "mov" -> "video/quicktime";
                default -> throw new IllegalArgumentException("Unsupported video type: " + extension);
            };
        }

        throw new IllegalArgumentException("Unsupported media type: " + type);
    }

    @Override
    public int compareTo(Media media) {
        Comparator<String> fileNameComparator = Comparator.naturalOrder();
        return fileNameComparator.compare(this.fileName, media.fileName);
    }

    /**
     * Enum representing the types of media: images and videos.
     */
    public enum MediaType {
        IMAGE, VIDEO;

        private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
        private static final List<String> VIDEO_EXTENSIONS = Arrays.asList("mp4", "webm", "ogg", "avi", "mov");

        /**
         * Determines the type of media based on the given file extension.
         * <p>
         * This method checks if the extension belongs to a known image or video type.
         * </p>
         *
         * @param extension the file extension (e.g., "jpg", "mp4").
         * @return the {@link MediaType} for the given extension, either {@link MediaType#IMAGE} or {@link MediaType#VIDEO}.
         * @throws IllegalArgumentException if the extension is unsupported.
         */
        public static MediaType of(String extension) {
            if (IMAGE_EXTENSIONS.contains(extension)) return MediaType.IMAGE;
            else if (VIDEO_EXTENSIONS.contains(extension)) return MediaType.VIDEO;
            else throw new IllegalArgumentException("Unsupported media type for an extension: " + extension);
        }
    }
}
