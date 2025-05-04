package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.videodownloader.VideoDownloadRequest;
import com.authenhub.bean.tool.videodownloader.VideoDownloadResponse;

/**
 * Interface for video downloader service operations
 */
public interface IVideoDownloaderService {
    
    /**
     * Download a video from YouTube
     * 
     * @param request the video download request
     * @return the video download response
     */
    VideoDownloadResponse downloadYouTubeVideo(VideoDownloadRequest request);
    
    /**
     * Download a video from TikTok
     * 
     * @param request the video download request
     * @return the video download response
     */
    VideoDownloadResponse downloadTikTokVideo(VideoDownloadRequest request);
    
    /**
     * Detect the platform from the URL and download the video
     * 
     * @param request the video download request
     * @return the video download response
     */
    VideoDownloadResponse downloadVideo(VideoDownloadRequest request);
}
