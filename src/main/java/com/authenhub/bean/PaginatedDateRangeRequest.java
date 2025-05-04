package com.authenhub.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedDateRangeRequest {
    private Integer page;
    private Integer size;
    private Timestamp startDate;
    private Timestamp endDate;
}
