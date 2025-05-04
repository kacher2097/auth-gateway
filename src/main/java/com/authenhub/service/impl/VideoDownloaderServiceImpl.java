package com.authenhub.service.impl;

import com.authenhub.bean.tool.videodownloader.VideoDownloadRequest;
import com.authenhub.bean.tool.videodownloader.VideoDownloadResponse;
import com.authenhub.service.interfaces.IVideoDownloaderService;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the video downloader service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoDownloaderServiceImpl implements IVideoDownloaderService {

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final String YOUTUBE_PATTERN = "(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com|youtu\\.be)\\/(?:watch\\?v=|shorts\\/)?([^\\s&]+)";
    private static final String TIKTOK_PATTERN = "(?:https?:\\/\\/)?(?:www\\.)?(?:tiktok\\.com)\\/(?:@[^\\/]+\\/video\\/|v\\/)([^\\s&\\?\\/]+)";

    @Override
    public VideoDownloadResponse downloadYouTubeVideo(VideoDownloadRequest request) {
        log.info("Downloading YouTube video: {}", request.getUrl());

        try {
            // Create a unique directory for this download
            String downloadId = UUID.randomUUID().toString();
            Path downloadDir = Paths.get(TEMP_DIR, "authenhub", "downloads", downloadId);
            Files.createDirectories(downloadDir);

            // Extract video ID from URL
            String videoId = extractYouTubeVideoId(request.getUrl());
            if (videoId == null) {
                return VideoDownloadResponse.builder()
                        .error("Invalid YouTube URL")
                        .build();
            }

            // Try with java-youtube-downloader first
            try {
                return downloadWithYoutubeDownloader(videoId, downloadDir.toFile(), request);
            } catch (Exception e) {
                log.warn("Failed to download with java-youtube-downloader, trying alternative method: {}", e.getMessage());
                // If that fails, try with alternative method
                try {
                    return downloadWithJavaTube(request.getUrl(), downloadDir.toString(), request);
                } catch (Exception e2) {
                    log.warn("Failed with alternative method, trying direct method: {}", e2.getMessage());
                    // If that also fails, try a direct method
                    return downloadYouTubeVideoDirectly(request.getUrl(), downloadDir.toFile(), videoId, request);
                }
            }

        } catch (Exception e) {
            log.error("Error downloading YouTube video", e);
            return VideoDownloadResponse.builder()
                    .error("Failed to download YouTube video: " + e.getMessage())
                    .build();
        }
    }

    private VideoDownloadResponse downloadWithYoutubeDownloader(String videoId, File downloadDir, VideoDownloadRequest request) throws YoutubeException, IOException {
        YoutubeDownloader downloader = new YoutubeDownloader();

        // Get video info
        RequestVideoInfo videoInfoRequest = new RequestVideoInfo(videoId);
        Response<VideoInfo> videoInfoResponse = downloader.getVideoInfo(videoInfoRequest);
        VideoInfo videoInfo = videoInfoResponse.data();

        // Select format based on quality preference
        Format format;
        if ("audio".equalsIgnoreCase(request.getFormat())) {
            format = videoInfo.bestAudioFormat();
        } else {
            List<VideoWithAudioFormat> formatsWithAudio = videoInfo.videoWithAudioFormats();
            if (!formatsWithAudio.isEmpty()) {
                if ("high".equalsIgnoreCase(request.getQuality())) {
                    format = videoInfo.bestVideoWithAudioFormat();
                } else if ("low".equalsIgnoreCase(request.getQuality())) {
                    format = formatsWithAudio.get(formatsWithAudio.size() - 1);
                } else {
                    format = videoInfo.bestVideoWithAudioFormat();
                }
            } else {
                format = videoInfo.bestVideoFormat();
            }
        }

        // Download the video
        RequestVideoFileDownload fileDownloadRequest = new RequestVideoFileDownload(format)
                .saveTo(downloadDir)
                .renameTo(videoId);

        Response<File> fileResponse = downloader.downloadVideoFile(fileDownloadRequest);
        File downloadedFile = fileResponse.data();

        // Generate download URL
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String downloadUrl = baseUrl + "/api/v1/tools/video-downloader/download/" + downloadDir.getName() + "/" + downloadedFile.getName();

        // Get file size
        long fileSize = downloadedFile.length();

        return VideoDownloadResponse.builder()
                .downloadUrl(downloadUrl)
                .title(videoInfo.details().title())
                .author(videoInfo.details().author())
                .duration((long) videoInfo.details().lengthSeconds())
                .size(fileSize)
                .format(format.extension().value())
                .quality(format instanceof VideoFormat ? ((VideoFormat) format).qualityLabel() : "audio")
                .thumbnailUrl(videoInfo.details().thumbnails().get(0))
                .build();
    }

    /**
     * Alternative implementation that doesn't use JavaTube library
     * This method uses a direct approach to download YouTube videos
     */
    private VideoDownloadResponse downloadWithJavaTube(String url, String downloadPath, VideoDownloadRequest request) {
        log.info("Using alternative method to download YouTube video: {}", url);

        try {
            // Extract video ID from URL
            String videoId = extractYouTubeVideoId(url);
            if (videoId == null) {
                throw new IOException("Invalid YouTube URL");
            }

            // Create a file to download to
            File downloadDir = new File(downloadPath);
            File downloadedFile = new File(downloadDir, videoId + ".mp4");

            // Set up headers to mimic a browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.youtube.com/");

            // Get the YouTube page content
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String html = response.getBody();

            if (html == null || html.isEmpty()) {
                throw new IOException("Failed to retrieve YouTube page content");
            }

            // Parse the HTML to extract video information
            Document doc = Jsoup.parse(html);

            // Extract video title
            String title = extractYouTubeTitle(doc);

            // Extract author name
            String author = extractYouTubeAuthor(doc);

            // Extract thumbnail URL
            String thumbnailUrl = extractYouTubeThumbnail(doc, videoId);

            // Extract video URL
            String videoUrl = extractYouTubeVideoUrl(doc, html);

            if (videoUrl == null || videoUrl.isEmpty()) {
                throw new IOException("Could not extract YouTube video URL from the page");
            }

            // Download the video
            downloadFile(videoUrl, downloadedFile, headers);

            // Generate download URL
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String downloadUrl = baseUrl + "/api/v1/tools/video-downloader/download/" +
                    Paths.get(downloadPath).getFileName() + "/" + downloadedFile.getName();

            // Get file size
            long fileSize = downloadedFile.length();

            return VideoDownloadResponse.builder()
                    .downloadUrl(downloadUrl)
                    .title(title != null ? title : "YouTube Video")
                    .author(author != null ? author : "YouTube User")
                    .duration(0L) // We don't know the duration from this method
                    .size(fileSize)
                    .format("mp4")
                    .quality(request.getQuality() != null ? request.getQuality() : "high")
                    .thumbnailUrl(thumbnailUrl != null ? thumbnailUrl : "")
                    .build();
        } catch (Exception e) {
            log.error("Error downloading with alternative method", e);
            throw new RuntimeException("Failed to download with alternative method: " + e.getMessage(), e);
        }
    }

    private String extractYouTubeVideoId(String url) {
        Pattern pattern = Pattern.compile(YOUTUBE_PATTERN);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public VideoDownloadResponse downloadTikTokVideo(VideoDownloadRequest request) {
        log.info("Downloading TikTok video: {}", request.getUrl());

        try {
            // Create a unique directory for this download
            String downloadId = UUID.randomUUID().toString();
            Path downloadDir = Paths.get(TEMP_DIR, "authenhub", "downloads", downloadId);
            Files.createDirectories(downloadDir);

            // Extract video ID from URL
            String videoId = extractTikTokVideoId(request.getUrl());
            if (videoId == null) {
                return VideoDownloadResponse.builder()
                        .error("Invalid TikTok URL")
                        .build();
            }

            // For TikTok, we'll use a direct approach to download the video
            // This is a simplified implementation that may need to be updated as TikTok changes their API
            try {
                return downloadTikTokVideoDirectly(request.getUrl(), downloadDir.toFile(), videoId);
            } catch (Exception e) {
                log.error("Error downloading TikTok video directly", e);
                return VideoDownloadResponse.builder()
                        .error("Failed to download TikTok video: " + e.getMessage())
                        .build();
            }

        } catch (Exception e) {
            log.error("Error downloading TikTok video", e);
            return VideoDownloadResponse.builder()
                    .error("Failed to download TikTok video: " + e.getMessage())
                    .build();
        }
    }

    private VideoDownloadResponse downloadTikTokVideoDirectly(String url, File downloadDir, String videoId) throws IOException {
        log.info("Downloading TikTok video directly from URL: {}", url);

        try {
            // Set up headers to mimic a browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.tiktok.com/");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Get the TikTok page content
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String html = response.getBody();

            if (html == null || html.isEmpty()) {
                throw new IOException("Failed to retrieve TikTok page content");
            }

            log.debug("Retrieved TikTok page HTML, length: {}", html.length());

            // Parse the HTML to extract video information
            Document doc = Jsoup.parse(html);

            // Extract video title
            String title = extractTikTokTitle(doc);
            log.info("Extracted TikTok title: {}", title);

            // Extract author name
            String author = extractTikTokAuthor(doc);
            log.info("Extracted TikTok author: {}", author);

            // Extract thumbnail URL
            String thumbnailUrl = extractTikTokThumbnail(doc);
            log.info("Extracted TikTok thumbnail URL: {}", thumbnailUrl);

            // Extract video URL - this is the most important part
            String videoUrl = extractTikTokVideoUrl(doc, html);

            if (videoUrl == null || videoUrl.isEmpty()) {
                throw new IOException("Could not extract TikTok video URL from the page");
            }

            log.info("Extracted TikTok video URL: {}", videoUrl);

            // Download the video
            File downloadedFile = new File(downloadDir, videoId + ".mp4");
            downloadFile(videoUrl, downloadedFile, headers);

            log.info("Downloaded TikTok video to: {}", downloadedFile.getAbsolutePath());

            // Generate download URL for the API response
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String downloadUrl = baseUrl + "/api/v1/tools/video-downloader/download/" + downloadDir.getName() + "/" + downloadedFile.getName();

            // Get file size
            long fileSize = downloadedFile.length();

            // Return the response
            return VideoDownloadResponse.builder()
                    .downloadUrl(downloadUrl)
                    .title(title != null ? title : "TikTok Video")
                    .author(author != null ? author : "TikTok User")
                    .duration(60L) // TikTok videos are typically short, but this is an estimate
                    .size(fileSize)
                    .format("mp4")
                    .quality("high")
                    .thumbnailUrl(thumbnailUrl != null ? thumbnailUrl : "")
                    .build();

        } catch (Exception e) {
            log.error("Error downloading TikTok video: {}", e.getMessage(), e);
            throw new IOException("Failed to download TikTok video: " + e.getMessage(), e);
        }
    }

    private String extractTikTokTitle(Document doc) {
        try {
            // Try different selectors that might contain the title
            Elements titleElements = doc.select("title");
            if (!titleElements.isEmpty()) {
                String title = titleElements.first().text();
                // Clean up the title (remove "| TikTok" suffix if present)
                if (title.contains("|")) {
                    return title.substring(0, title.lastIndexOf("|")).trim();
                }
                return title;
            }

            // Try meta tags
            Elements metaTags = doc.select("meta[property=og:title]");
            if (!metaTags.isEmpty()) {
                return metaTags.attr("content");
            }

            return "TikTok Video";
        } catch (Exception e) {
            log.warn("Could not extract TikTok title: {}", e.getMessage());
            return "TikTok Video";
        }
    }

    private String extractTikTokAuthor(Document doc) {
        try {
            // Try to find the author from meta tags
            Elements authorElements = doc.select("meta[property=og:author]");
            if (!authorElements.isEmpty()) {
                return authorElements.attr("content");
            }

            // Try to find from the URL
            Elements canonicalElements = doc.select("link[rel=canonical]");
            if (!canonicalElements.isEmpty()) {
                String canonicalUrl = canonicalElements.attr("href");
                Pattern pattern = Pattern.compile("@([^/]+)");
                Matcher matcher = pattern.matcher(canonicalUrl);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }

            return "TikTok User";
        } catch (Exception e) {
            log.warn("Could not extract TikTok author: {}", e.getMessage());
            return "TikTok User";
        }
    }

    private String extractTikTokThumbnail(Document doc) {
        try {
            // Try to find the thumbnail from meta tags
            Elements thumbnailElements = doc.select("meta[property=og:image]");
            if (!thumbnailElements.isEmpty()) {
                return thumbnailElements.attr("content");
            }

            return null;
        } catch (Exception e) {
            log.warn("Could not extract TikTok thumbnail: {}", e.getMessage());
            return null;
        }
    }

    private String extractTikTokVideoUrl(Document doc, String html) {
        try {
            // Method 1: Try to find the video URL from meta tags
            Elements videoElements = doc.select("meta[property=og:video]");
            if (!videoElements.isEmpty()) {
                String videoUrl = videoElements.attr("content");
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    return videoUrl;
                }
            }

            // Method 2: Try to find the video URL from video tags
            Elements videoTags = doc.select("video source");
            if (!videoTags.isEmpty()) {
                String videoUrl = videoTags.attr("src");
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    return videoUrl;
                }
            }

            // Method 3: Try to extract from JSON data in the page
            Pattern pattern = Pattern.compile("\"playAddr\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                String videoUrl = matcher.group(1);
                // Unescape the URL
                videoUrl = videoUrl.replace("\\", "");
                return videoUrl;
            }

            // Method 4: Try another JSON pattern
            pattern = Pattern.compile("\"downloadAddr\":\"([^\"]+)\"");
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                String videoUrl = matcher.group(1);
                // Unescape the URL
                videoUrl = videoUrl.replace("\\", "");
                return videoUrl;
            }

            // Method 5: Look for __NEXT_DATA__ JSON
            pattern = Pattern.compile("<script id=\"__NEXT_DATA__\" type=\"application/json\">(.*?)</script>", Pattern.DOTALL);
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                String jsonData = matcher.group(1);
                // Look for video URL in the JSON data
                Pattern videoPattern = Pattern.compile("\"playAddr\":\"([^\"]+)\"");
                Matcher videoMatcher = videoPattern.matcher(jsonData);
                if (videoMatcher.find()) {
                    String videoUrl = videoMatcher.group(1);
                    // Unescape the URL
                    videoUrl = videoUrl.replace("\\", "");
                    return videoUrl;
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("Could not extract TikTok video URL: {}", e.getMessage());
            return null;
        }
    }

    private VideoDownloadResponse downloadYouTubeVideoDirectly(String url, File downloadDir, String videoId, VideoDownloadRequest request) {
        log.info("Downloading YouTube video directly from URL: {}", url);

        try {
            // Set up headers to mimic a browser request
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            headers.set("Accept-Language", "en-US,en;q=0.9");
            headers.set("Referer", "https://www.youtube.com/");

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Get the YouTube page content
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String html = response.getBody();

            if (html == null || html.isEmpty()) {
                throw new IOException("Failed to retrieve YouTube page content");
            }

            log.debug("Retrieved YouTube page HTML, length: {}", html.length());

            // Parse the HTML to extract video information
            Document doc = Jsoup.parse(html);

            // Extract video title
            String title = extractYouTubeTitle(doc);
            log.info("Extracted YouTube title: {}", title);

            // Extract author name
            String author = extractYouTubeAuthor(doc);
            log.info("Extracted YouTube author: {}", author);

            // Extract thumbnail URL
            String thumbnailUrl = extractYouTubeThumbnail(doc, videoId);
            log.info("Extracted YouTube thumbnail URL: {}", thumbnailUrl);

            // Extract video URL - this is the most important part
            String videoUrl = extractYouTubeVideoUrl(doc, html);

            if (videoUrl == null || videoUrl.isEmpty()) {
                throw new IOException("Could not extract YouTube video URL from the page");
            }

            log.info("Extracted YouTube video URL: {}", videoUrl);

            // Download the video
            File downloadedFile = new File(downloadDir, videoId + ".mp4");
            downloadFile(videoUrl, downloadedFile, headers);

            log.info("Downloaded YouTube video to: {}", downloadedFile.getAbsolutePath());

            // Generate download URL for the API response
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String downloadUrl = baseUrl + "/api/v1/tools/video-downloader/download/" + downloadDir.getName() + "/" + downloadedFile.getName();

            // Get file size
            long fileSize = downloadedFile.length();

            // Return the response
            return VideoDownloadResponse.builder()
                    .downloadUrl(downloadUrl)
                    .title(title != null ? title : "YouTube Video")
                    .author(author != null ? author : "YouTube User")
                    .duration(0L) // We don't know the duration from this method
                    .size(fileSize)
                    .format("mp4")
                    .quality(request.getQuality() != null ? request.getQuality() : "high")
                    .thumbnailUrl(thumbnailUrl != null ? thumbnailUrl : "")
                    .build();

        } catch (Exception e) {
            log.error("Error downloading YouTube video directly: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to download YouTube video: " + e.getMessage(), e);
        }
    }

    private String extractYouTubeTitle(Document doc) {
        try {
            // Try different selectors that might contain the title
            Elements titleElements = doc.select("title");
            if (!titleElements.isEmpty()) {
                String title = titleElements.first().text();
                // Clean up the title (remove "- YouTube" suffix if present)
                if (title.contains("- YouTube")) {
                    return title.substring(0, title.lastIndexOf("- YouTube")).trim();
                }
                return title;
            }

            // Try meta tags
            Elements metaTags = doc.select("meta[property=og:title]");
            if (!metaTags.isEmpty()) {
                return metaTags.attr("content");
            }

            return "YouTube Video";
        } catch (Exception e) {
            log.warn("Could not extract YouTube title: {}", e.getMessage());
            return "YouTube Video";
        }
    }

    private String extractYouTubeAuthor(Document doc) {
        try {
            // Try to find the author from meta tags
            Elements authorElements = doc.select("meta[name=author]");
            if (!authorElements.isEmpty()) {
                return authorElements.attr("content");
            }

            // Try another selector
            Elements channelElements = doc.select(".ytd-channel-name a");
            if (!channelElements.isEmpty()) {
                return channelElements.text();
            }

            return "YouTube User";
        } catch (Exception e) {
            log.warn("Could not extract YouTube author: {}", e.getMessage());
            return "YouTube User";
        }
    }

    private String extractYouTubeThumbnail(Document doc, String videoId) {
        try {
            // Try to find the thumbnail from meta tags
            Elements thumbnailElements = doc.select("meta[property=og:image]");
            if (!thumbnailElements.isEmpty()) {
                return thumbnailElements.attr("content");
            }

            // If not found, construct the URL from the video ID
            return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        } catch (Exception e) {
            log.warn("Could not extract YouTube thumbnail: {}", e.getMessage());
            return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";
        }
    }

    private String extractYouTubeVideoUrl(Document doc, String html) {
        try {
            // Method 1: Try to find the video URL from meta tags
            Elements videoElements = doc.select("meta[property=og:video]");
            if (!videoElements.isEmpty()) {
                String videoUrl = videoElements.attr("content");
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    return videoUrl;
                }
            }

            // Method 2: Try to find the video URL from video tags
            Elements videoTags = doc.select("video source");
            if (!videoTags.isEmpty()) {
                String videoUrl = videoTags.attr("src");
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    return videoUrl;
                }
            }

            // Method 3: Try to extract from JSON data in the page
            Pattern pattern = Pattern.compile("\"url\":\"([^\"]+\\\\.mp4[^\"]*)\"");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                String videoUrl = matcher.group(1);
                // Unescape the URL
                videoUrl = videoUrl.replace("\\", "");
                return videoUrl;
            }

            // Method 4: Look for player_response JSON
            pattern = Pattern.compile("\"player_response\":\"([^\"]+)\"");
            matcher = pattern.matcher(html);
            if (matcher.find()) {
                String playerResponse = matcher.group(1);
                // Unescape the JSON
                playerResponse = playerResponse.replace("\\", "");
                // Look for streaming URLs
                Pattern urlPattern = Pattern.compile("\"url\":\"([^\"]+)\"");
                Matcher urlMatcher = urlPattern.matcher(playerResponse);
                if (urlMatcher.find()) {
                    return urlMatcher.group(1);
                }
            }

            return null;
        } catch (Exception e) {
            log.warn("Could not extract YouTube video URL: {}", e.getMessage());
            return null;
        }
    }

    private void downloadFile(String fileUrl, File destination, HttpHeaders headers) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set headers
        connection.setRequestProperty("User-Agent", headers.getFirst("User-Agent"));
        connection.setRequestProperty("Referer", headers.getFirst("Referer"));
        connection.setRequestProperty("Accept-Language", headers.getFirst("Accept-Language"));

        // Set up the connection
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(30000);

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private String extractTikTokVideoId(String url) {
        Pattern pattern = Pattern.compile(TIKTOK_PATTERN);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public VideoDownloadResponse downloadVideo(VideoDownloadRequest request) {
        String url = request.getUrl();

        if (url.matches(YOUTUBE_PATTERN)) {
            return downloadYouTubeVideo(request);
        } else if (url.matches(TIKTOK_PATTERN)) {
            return downloadTikTokVideo(request);
        } else {
            return VideoDownloadResponse.builder()
                    .error("Unsupported video URL. Only YouTube and TikTok URLs are supported.")
                    .build();
        }
    }
}
