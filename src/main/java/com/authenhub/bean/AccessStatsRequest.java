package com.authenhub.bean;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessStatsRequest {
    private Timestamp startDate;
    private Timestamp endDate;
}
