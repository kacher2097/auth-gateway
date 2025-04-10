package com.authenhub.exception;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends AuthException {
    public PasswordMismatchException() {
        super("Mật khẩu xác nhận không khớp", HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH");
    }
}
