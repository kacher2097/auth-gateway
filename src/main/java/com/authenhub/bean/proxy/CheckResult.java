package com.authenhub.bean.proxy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckResult {
    private String id;
    private boolean isWorking;
    private long responseTimeMs;
    private Timestamp checkedAt;
}
