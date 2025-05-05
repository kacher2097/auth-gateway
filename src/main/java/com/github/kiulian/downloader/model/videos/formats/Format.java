package com.github.kiulian.downloader.model.videos.formats;

/**
 * Base interface for all video and audio formats
 */
public interface Format {
    /**
     * Get the itag of the format
     * @return The itag
     */
    String itag();

    /**
     * Get the URL of the format
     * @return The URL
     */
    String url();

    /**
     * Get the MIME type of the format
     * @return The MIME type
     */
    String mimeType();

    /**
     * Get the file extension of the format
     * @return The file extension
     */
    Extension extension();

    /**
     * Enum for file extensions
     */
    enum Extension {
        MP4("mp4"),
        M4A("m4a"),
        WEBM("webm"),
        THREE_GP("3gp"),
        FLV("flv"),
        UNKNOWN("unknown");

        private final String value;

        Extension(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
