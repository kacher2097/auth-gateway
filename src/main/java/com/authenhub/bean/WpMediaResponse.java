package com.authenhub.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WpMediaResponse {
    private Long id;
    private String date;
    private String modified;
    private String slug;
    private String status;
    private String type;
    private String link;

    @JsonIgnore
    private String title;
    private Long author;

    @JsonProperty("featured_media")
    private Long featuredMedia;

    @JsonProperty("comment_status")
    private String commentStatus;

    @JsonProperty("ping_status")
    private String pingStatus;

    private String template;
    private String permalinkTemplate;

    @JsonProperty("generated_slug")
    private String generatedSlug;

    @JsonProperty("source_url")
    private String sourceUrl;

//    private MediaDetails mediaDetails;

//    @JsonProperty("meta")
//    private Map<String, Object> meta;
//
//    @JsonProperty("smush")
//    private Map<String, Object> smush;
//
//    @JsonProperty("_links")
//    private Map<String, Object> links;

//    @Data
//    public static class MediaDetails {
//        private int width;
//        private int height;
//        private String file;
//        private long filesize;
//        private Map<String, ImageSize> sizes;
//
//        @JsonProperty("image_meta")
//        private ImageMeta imageMeta;
//    }
//
//    @Data
//    public static class ImageSize {
//        private String file;
//        private int width;
//        private int height;
//
//        @JsonProperty("filesize")
//        private Long fileSize;
//
//        @JsonProperty("mime_type")
//        private String mimeType;
//
//        @JsonProperty("source_url")
//        private String sourceUrl;
//    }
//
//    @Data
//    public static class ImageMeta {
//        private String aperture;
//        private String credit;
//        private String camera;
//
//        @JsonProperty("created_timestamp")
//        private Long createdTimestamp;
//
//        private String copyright;
//
//        @JsonProperty("focal_length")
//        private String focalLength;
//
//        private String iso;
//
//        @JsonProperty("shutter_speed")
//        private String shutterSpeed;
//
//        private String title;
//        private String orientation;
//        private List<String> keywords;
//    }
}

