package com.authenhub.bean.tool.apitester;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTestRequest {
    private String userId;
    private String method;
    private String url;
    private String contentType;
    private String body;
    private List<RequestHeader> headers;
    private Integer timeout;
    private Boolean followRedirects;
}
