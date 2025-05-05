package com.authenhub.bean.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginActivityBean {
    private String username;
    private String ip;
    private String status;
    private String timestamp;
    private String userAgent;
    private String reason;
}
