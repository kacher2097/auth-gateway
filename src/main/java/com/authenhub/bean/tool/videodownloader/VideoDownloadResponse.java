package com.authenhub.bean.tool.videodownloader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response model for video download
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDownloadResponse {
    
    /**
     * URL of the downloaded video
     */
    private String downloadUrl;
    private String title;
    private String author;
    
    /**
     * Duration of the video in seconds
     */
    private Long duration;
    
    /**
     * Size of the video in bytes
     */
    private Long size;
    
    /**
     * Format of the video (e.g., mp4, mp3)
     */
    private String format;
    
    /**
     * Quality of the video (e.g., high, medium, low)
     */
    private String quality;
    private String thumbnailUrl;
    private String error;
}
