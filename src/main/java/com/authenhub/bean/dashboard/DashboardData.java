package com.authenhub.bean.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    private long totalUsers;
    private long adminUsers;
    private long regularUsers;
}
