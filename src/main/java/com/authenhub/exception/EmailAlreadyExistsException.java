package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AuthException {
    public EmailAlreadyExistsException() {
        super("Email đã tồn tại", HttpStatus.CONFLICT, "EMAIL_EXISTS");
    }
}
