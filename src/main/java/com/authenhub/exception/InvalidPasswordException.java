package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends AuthException {
    public InvalidPasswordException() {
        super("Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST, "INVALID_PASSWORD");
    }
}
