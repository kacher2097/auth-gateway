package com.github.kiulian.downloader.model.videos.formats;

/**
 * Interface for video formats
 */
public interface VideoFormat extends Format {
    /**
     * Get the quality label of the video format
     * @return The quality label (e.g. "720p")
     */
    String qualityLabel();

    /**
     * Get the width of the video
     * @return The width in pixels
     */
    int width();

    /**
     * Get the height of the video
     * @return The height in pixels
     */
    int height();

    /**
     * Get the frame rate of the video
     * @return The frame rate in frames per second
     */
    int fps();
}
