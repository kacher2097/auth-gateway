package com.authenhub.bean.tool.apitester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTestResponse {
    private Integer statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String body;
    private String responseTime;
    private Long size;
    private String contentType;
    private String error;
}
