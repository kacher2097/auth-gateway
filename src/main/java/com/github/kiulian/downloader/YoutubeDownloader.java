package com.github.kiulian.downloader;

import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;

import java.io.File;

/**
 * Main class for interacting with the YouTube downloader
 */
public class YoutubeDownloader {

    /**
     * Get information about a YouTube video
     * @param request The request containing the video ID
     * @return A response containing the video information
     * @throws YoutubeException If an error occurs
     */
    public Response<VideoInfo> getVideoInfo(RequestVideoInfo request) throws YoutubeException {
        // Implementation would normally go here
        // For now, we'll throw an exception to indicate this is a stub
        throw new YoutubeException.BadPageException("This is a stub implementation. Please use the actual library.");
    }

    /**
     * Download a YouTube video file
     * @param request The request containing the format and save location
     * @return A response containing the downloaded file
     * @throws YoutubeException If an error occurs
     */
    public Response<File> downloadVideoFile(RequestVideoFileDownload request) throws YoutubeException {
        // Implementation would normally go here
        // For now, we'll throw an exception to indicate this is a stub
        throw new YoutubeException.BadPageException("This is a stub implementation. Please use the actual library.");
    }
}
