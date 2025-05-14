package com.authenhub.constant.enums;

import com.authenhub.constant.IApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiResponseCode implements IApiResponse {

    EXCEPTION_CODE("99", "Internal Server Error"),
    FORBIDDEN("13", "Forbidden"),
    SUCCESS_CODE("00", "Success"),
    ;
    private final String code;
    private final String message;

    @Override
    public String getCode() {
        return this.code;
    }
    @Override
    public String getMessage() {
        return this.message;
    }


}
