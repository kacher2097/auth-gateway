package com.github.kiulian.downloader.downloader.request;

import com.github.kiulian.downloader.model.videos.formats.Format;

import java.io.File;

/**
 * Request for downloading a YouTube video file
 */
public class RequestVideoFileDownload {
    private final Format format;
    private File outputDirectory;
    private String outputFileName;

    public RequestVideoFileDownload(Format format) {
        this.format = format;
    }

    public Format getFormat() {
        return format;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public RequestVideoFileDownload saveTo(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }

    public RequestVideoFileDownload renameTo(String outputFileName) {
        this.outputFileName = outputFileName;
        return this;
    }
}
