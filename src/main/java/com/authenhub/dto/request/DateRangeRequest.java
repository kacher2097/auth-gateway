package com.authenhub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Common request DTO for date range operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequest {
    private Timestamp startDate;
    private Timestamp endDate;
}
