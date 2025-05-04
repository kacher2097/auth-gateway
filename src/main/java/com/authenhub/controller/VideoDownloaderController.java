package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.videodownloader.VideoDownloadRequest;
import com.authenhub.bean.tool.videodownloader.VideoDownloadResponse;
import com.authenhub.service.interfaces.IVideoDownloaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Controller for video downloader operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tools/video-downloader")
@RequiredArgsConstructor
public class VideoDownloaderController {

    private final IVideoDownloaderService videoDownloaderService;

    /**
     * Download a video from YouTube or TikTok
     *
     * @param request The video download request
     * @return The video download response
     */
    @PostMapping
    public ApiResponse<VideoDownloadResponse> downloadVideo(@Valid @RequestBody VideoDownloadRequest request) {
        log.info("Received request to download video: {}", request.getUrl());
        VideoDownloadResponse response = videoDownloaderService.downloadVideo(request);
        return ApiResponse.success(response);
    }

    /**
     * Download a video from YouTube
     *
     * @param request The video download request
     * @return The video download response
     */
    @PostMapping("/youtube")
    public ApiResponse<VideoDownloadResponse> downloadYouTubeVideo(@Valid @RequestBody VideoDownloadRequest request) {
        log.info("Received request to download YouTube video: {}", request.getUrl());
        VideoDownloadResponse response = videoDownloaderService.downloadYouTubeVideo(request);
        return ApiResponse.success(response);
    }

    /**
     * Download a video from TikTok
     *
     * @param request The video download request
     * @return The video download response
     */
    @PostMapping("/tiktok")
    public ApiResponse<VideoDownloadResponse> downloadTikTokVideo(@Valid @RequestBody VideoDownloadRequest request) {
        log.info("Received request to download TikTok video: {}", request.getUrl());
        VideoDownloadResponse response = videoDownloaderService.downloadTikTokVideo(request);
        return ApiResponse.success(response);
    }

    /**
     * Download a video file
     *
     * @param folderId The folder ID containing the video
     * @param fileName The name of the video file
     * @return The video file as a resource
     */
    @GetMapping("/download/{folderId}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String folderId, @PathVariable String fileName) {
        log.info("Received request to download file: {}/{}", folderId, fileName);

        try {
            // Construct the file path
            String tempDir = System.getProperty("java.io.tmpdir");
            File file = Paths.get(tempDir, "authenhub", "downloads", folderId, fileName).toFile();

            // Check if the file exists
            if (!file.exists()) {
                log.error("File not found: {}", file.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            // Create a resource from the file
            Resource resource = new FileSystemResource(file);

            // Determine the content type
            String contentType = determineContentType(fileName);

            // Build the response
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Determine the content type based on the file extension
     *
     * @param fileName The name of the file
     * @return The content type
     */
    private String determineContentType(String fileName) {
        if (fileName.endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        } else if (fileName.endsWith(".webm")) {
            return "video/webm";
        } else {
            return "application/octet-stream";
        }
    }
}
