package com.authenhub.exception;

import com.authenhub.constant.IApiResponse;
import lombok.Getter;

public class ErrorApiException extends RuntimeException {

    @Getter
    private final String code;
    private Object data;

    public ErrorApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorApiException(IApiResponse responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ErrorApiException(IApiResponse responseCode, Object data) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.data = data;
    }

}
