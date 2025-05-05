package com.github.kiulian.downloader.downloader.request;

/**
 * Request for getting information about a YouTube video
 */
public class RequestVideoInfo {
    private final String videoId;

    public RequestVideoInfo(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }
}
