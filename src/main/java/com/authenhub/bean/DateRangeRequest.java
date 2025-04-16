package com.authenhub.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DateRangeRequest {
    private Timestamp startDate;
    private Timestamp endDate;
}
