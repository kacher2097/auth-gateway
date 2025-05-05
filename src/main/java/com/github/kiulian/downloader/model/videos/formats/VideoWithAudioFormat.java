package com.github.kiulian.downloader.model.videos.formats;

/**
 * Interface for video formats that also contain audio
 */
public interface VideoWithAudioFormat extends VideoFormat {
    // This interface extends VideoFormat and doesn't add any new methods
    // It's used to distinguish between video-only formats and formats that contain both video and audio
}
