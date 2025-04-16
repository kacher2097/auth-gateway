package com.authenhub.bean;

import lombok.Data;

@Data
public class PaginatedDateRangeRequest extends DateRangeRequest {
    private Integer page;
    private Integer size;
}
