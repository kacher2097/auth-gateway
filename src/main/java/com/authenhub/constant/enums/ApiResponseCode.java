package com.authenhub.constant.enums;

import com.authenhub.constant.IApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApiResponseCode implements IApiResponse {

    EXEPTION_CODE("99", "EXEPTION"),
    SUCCESS_CODE("00", "SUCCESS"),
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
