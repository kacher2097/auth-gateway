package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends AuthException {
    public EmailNotFoundException() {
        super("Email không tồn tại trong hệ thống", HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND");
    }
}
