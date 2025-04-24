package com.authenhub.bean.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private Object errors;
}
