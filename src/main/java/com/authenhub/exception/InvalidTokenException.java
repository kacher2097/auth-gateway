package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends AuthException {
    public InvalidTokenException() {
        super("Token không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST, "INVALID_TOKEN");
    }
}
