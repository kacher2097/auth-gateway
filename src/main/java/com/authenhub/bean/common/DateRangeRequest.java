package com.authenhub.bean.common;

import com.authenhub.config.TimestampDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeRequest {
    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp startDate;

    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp endDate;
}
