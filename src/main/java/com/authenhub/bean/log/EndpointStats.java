package com.authenhub.bean.log;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointStats {
    private String id; // Endpoint
    private Long count;
    private Double avgResponseTime;
}
