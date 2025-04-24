package com.authenhub.bean.proxy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyRequest {
//    @NotBlank(message = "IP address is required")
    private String ipAddress;

//    @NotNull(message = "Port is required")
//    @Min(value = 1, message = "Port must be greater than 0")
//    @Max(value = 65535, message = "Port must be less than 65536")
    private Integer port;

//    @NotBlank(message = "Protocol is required")
    private String protocol;

    private String country;
    private String city;
    private String notes;
}
