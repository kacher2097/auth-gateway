package com.github.kiulian.downloader.downloader.response;

/**
 * Response from the YouTube downloader
 * @param <T> The type of data in the response
 */
public class Response<T> {
    private final T data;
    private final boolean successful;
    private final String errorMessage;

    private Response(T data, boolean successful, String errorMessage) {
        this.data = data;
        this.successful = successful;
        this.errorMessage = errorMessage;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(data, true, null);
    }

    public static <T> Response<T> error(String errorMessage) {
        return new Response<>(null, false, errorMessage);
    }

    public T data() {
        return data;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
