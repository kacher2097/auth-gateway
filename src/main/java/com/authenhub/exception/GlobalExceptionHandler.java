package com.authenhub.exception;

import com.authenhub.dto.ErrorResponse;
import com.authenhub.utils.TimestampUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .code("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DataAccessException.class, UncategorizedMongoDbException.class})
    public ResponseEntity<ErrorResponse> handleDataAccessException(Exception ex, HttpServletRequest request) {
        log.error("Database access error", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("A database error occurred. Please try again later.")
                .code("DATABASE_ERROR")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, HttpServletRequest request) {
        log.error("Null pointer exception", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred. Please try again later.")
                .code("NULL_POINTER_ERROR")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .code("RESOURCE_NOT_FOUND")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .code("RESOURCE_ALREADY_EXISTS")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message("You do not have permission to access this resource")
                .code("ACCESS_DENIED")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(TimestampUtils.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .code("ILLEGAL_STATE")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleApiError(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);

        if (ex instanceof ErrorApiException errorApiException) {
            log.debug("Handled exception: ", ex);
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .message(errorApiException.getMessage())
                    .code(errorApiException.getCode())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.ok(errorResponse);
        }
        ErrorResponse errorResponse;
        switch (ex) {
            case AuthException authException:
                log.debug("Handled exception: ", ex);
                errorResponse = ErrorResponse.builder()
                        .message(authException.getMessage())
                        .code("89")
                        .path(request.getRequestURI())
                        .build();
                return ResponseEntity.ok(errorResponse);
            case IllegalArgumentException illegalArgumentException:
                log.debug("Handled exception: ", ex);
                errorResponse = ErrorResponse.builder()
                        .code("10")
                        .message(illegalArgumentException.getMessage())
                        .path(request.getRequestURI())
                        .build();
                return ResponseEntity.ok(errorResponse);
            default:
                errorResponse = ErrorResponse.builder()
                        .code("99")
                        .message("An unexpected error occurred. Please try again later.")
                        .path(request.getRequestURI())
                        .build();
                return ResponseEntity.ok(errorResponse);
        }
    }
}
