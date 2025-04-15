package com.authenhub.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final Timestamp timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String errorCode;
    private final String path;
    private final Map<String, String> fieldErrors;
}
