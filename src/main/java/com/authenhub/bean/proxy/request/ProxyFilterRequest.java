package com.authenhub.bean.proxy.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common request DTO for proxy filtering operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyFilterRequest {
    private String protocol;
    private String country;
    private Integer maxResponseTime;
    private Double minUptime;
    private Boolean active;
    private String sortBy;
    private String sortDirection;
}
