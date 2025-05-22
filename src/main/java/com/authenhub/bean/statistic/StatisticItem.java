package com.authenhub.bean.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticItem {
    private String username;
    private String id;
    private String name;
    private Long count;
    private Double avgResponseTime;
}
