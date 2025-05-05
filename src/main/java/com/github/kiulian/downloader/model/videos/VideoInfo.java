package com.github.kiulian.downloader.model.videos;

import com.github.kiulian.downloader.model.videos.formats.Format;
import com.github.kiulian.downloader.model.videos.formats.VideoFormat;
import com.github.kiulian.downloader.model.videos.formats.VideoWithAudioFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about a YouTube video
 */
public class VideoInfo {
    private final VideoDetails details;
    private final List<Format> formats;

    public VideoInfo(VideoDetails details, List<Format> formats) {
        this.details = details;
        this.formats = formats;
    }

    public VideoDetails details() {
        return details;
    }

    public List<Format> formats() {
        return formats;
    }

    public Format bestAudioFormat() {
        // Stub implementation
        return new Format() {
            @Override
            public String itag() {
                return "140";
            }

            @Override
            public String url() {
                return "https://example.com/audio.mp4";
            }

            @Override
            public String mimeType() {
                return "audio/mp4";
            }

            @Override
            public Extension extension() {
                return Extension.M4A;
            }
        };
    }

    public VideoFormat bestVideoFormat() {
        // Stub implementation
        return new VideoFormat() {
            @Override
            public String qualityLabel() {
                return "720p";
            }

            @Override
            public int width() {
                return 1280;
            }

            @Override
            public int height() {
                return 720;
            }

            @Override
            public int fps() {
                return 30;
            }

            @Override
            public String itag() {
                return "136";
            }

            @Override
            public String url() {
                return "https://example.com/video.mp4";
            }

            @Override
            public String mimeType() {
                return "video/mp4";
            }

            @Override
            public Extension extension() {
                return Extension.MP4;
            }
        };
    }

    public VideoWithAudioFormat bestVideoWithAudioFormat() {
        // Stub implementation
        return new VideoWithAudioFormat() {
            @Override
            public String qualityLabel() {
                return "720p";
            }

            @Override
            public int width() {
                return 1280;
            }

            @Override
            public int height() {
                return 720;
            }

            @Override
            public int fps() {
                return 30;
            }

            @Override
            public String itag() {
                return "22";
            }

            @Override
            public String url() {
                return "https://example.com/video_with_audio.mp4";
            }

            @Override
            public String mimeType() {
                return "video/mp4";
            }

            @Override
            public Extension extension() {
                return Extension.MP4;
            }
        };
    }

    public List<VideoWithAudioFormat> videoWithAudioFormats() {
        List<VideoWithAudioFormat> result = new ArrayList<>();
        result.add(bestVideoWithAudioFormat());
        return result;
    }
}
