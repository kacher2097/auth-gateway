package com.authenhub.bean;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class DateRangeRequest {
    private Timestamp startDate;
    private Timestamp endDate;
}
