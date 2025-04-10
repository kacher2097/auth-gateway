package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends AuthException {
    public UsernameAlreadyExistsException() {
        super("Username đã tồn tại", HttpStatus.CONFLICT, "USERNAME_EXISTS");
    }
}
