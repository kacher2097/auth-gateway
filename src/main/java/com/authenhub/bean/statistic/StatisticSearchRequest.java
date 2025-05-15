package com.authenhub.bean.statistic;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticSearchRequest {
    private String keyword;
    private Timestamp startDate;

    //    @JsonDeserialize(using = TimestampDeserializer.class)
    private Timestamp endDate;
}
