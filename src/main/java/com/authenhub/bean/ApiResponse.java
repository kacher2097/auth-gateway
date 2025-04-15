package com.authenhub.bean;

import com.authenhub.constant.enums.ApiResponseCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@JsonPropertyOrder({"code", "message", "data", "metadata"})
public class ApiResponse<T> {

    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, Object> metadata;

    public ApiResponse(String code, String message, T data, Map<String, Object> metadata) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.metadata = metadata;
    }

    // Builder class
    public static class Builder<T> {
        private final String code;
        private final String message;
        private T data;
        private Map<String, Object> metadata;

        public Builder(String code, String message) {
            this.code = code;
            this.message = message;
            this.metadata = Collections.emptyMap();
        }

        public Builder(ApiResponseCode responseCode) {
            this.code = responseCode.getCode();
            this.message = responseCode.getMessage();
            this.metadata = Collections.emptyMap();
        }

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> metadata(Map<String, Object> metadata) {
            this.metadata = (metadata != null) ? metadata : Collections.emptyMap();
            return this;
        }

        public ApiResponse<T> build() {
            return new ApiResponse<>(code, message, data, metadata);
        }
    }

    // Static factory methods
    public static <T> ApiResponse<T> error(String code, String message) {
        return new Builder<T>(code, message).build();
    }

    public static <T> ApiResponse<T> of(String code, String message, T data) {
        return new Builder<T>(code, message).data(data).build();
    }

    public static <T> ApiResponse<T> of(String code, String message, T data, Map<String, Object> metadata) {
        return new Builder<T>(code, message).data(data).metadata(metadata).build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new Builder<T>(ApiResponseCode.SUCCESS_CODE).data(data).build();
    }

    public static <T> ApiResponse<T> error(ApiResponseCode errorCode) {
        return new Builder<T>(errorCode).build();
    }

    public static <T> ApiResponse<T> error(ApiResponseCode errorCode, String customMessage) {
        return new Builder<T>(errorCode.getCode(), customMessage).build();
    }

    public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T data) {
        return new Builder<T>(responseCode).data(data).build();
    }

    public static <T> ApiResponse<T> of(ApiResponseCode responseCode, T data, Map<String, Object> metadata) {
        return new Builder<T>(responseCode).data(data).metadata(metadata).build();
    }
}
