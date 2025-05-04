package com.authenhub.bean.statistic;

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
public class StatisticSearchRequest {
    private String keyword;

    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp startDate;

    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp endDate;
}
