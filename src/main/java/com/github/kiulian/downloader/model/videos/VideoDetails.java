package com.github.kiulian.downloader.model.videos;

import java.util.ArrayList;
import java.util.List;

/**
 * Details about a YouTube video
 */
public class VideoDetails {
    private final String title;
    private final String author;
    private final int lengthSeconds;
    private final List<String> thumbnails;

    public VideoDetails(String title, String author, int lengthSeconds, List<String> thumbnails) {
        this.title = title;
        this.author = author;
        this.lengthSeconds = lengthSeconds;
        this.thumbnails = thumbnails;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public int lengthSeconds() {
        return lengthSeconds;
    }

    public List<String> thumbnails() {
        return thumbnails;
    }

    // Stub implementation for testing
    public static VideoDetails createStub() {
        List<String> thumbnails = new ArrayList<>();
        thumbnails.add("https://i.ytimg.com/vi/dQw4w9WgXcQ/default.jpg");
        return new VideoDetails("Never Gonna Give You Up", "Rick Astley", 213, thumbnails);
    }
}
