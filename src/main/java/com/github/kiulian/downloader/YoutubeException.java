package com.github.kiulian.downloader;

/**
 * Exception thrown by the YouTube downloader
 */
public class YoutubeException extends Exception {

    public YoutubeException(String message) {
        super(message);
    }

    public YoutubeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception thrown when the YouTube page cannot be parsed
     */
    public static class BadPageException extends YoutubeException {
        public BadPageException(String message) {
            super(message);
        }

        public BadPageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Exception thrown when a requested format is not found
     */
    public static class FormatNotFoundException extends YoutubeException {
        public FormatNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * Exception thrown when a video cannot be downloaded
     */
    public static class VideoUnplayableException extends YoutubeException {
        public VideoUnplayableException(String message) {
            super(message);
        }
    }
}
