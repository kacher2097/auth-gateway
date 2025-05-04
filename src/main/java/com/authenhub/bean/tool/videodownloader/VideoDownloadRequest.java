package com.authenhub.bean.tool.videodownloader;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Request model for video download
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDownloadRequest {
    
    /**
     * URL of the video to download
     */
    @NotBlank(message = "Video URL is required")
    private String url;
    
    /**
     * Optional format for the video (e.g., mp4, mp3)
     */
    private String format;
    
    /**
     * Optional quality for the video (e.g., high, medium, low)
     */
    private String quality;
    
    /**
     * User ID for tracking purposes
     */
    private String userId;
}
