package com.authenhub.controller;

import lombok.Data;

import java.util.Date;

@Data
public class PaginatedDateRangeRequest extends DateRangeRequest {
    private Integer page;
    private Integer size;
}
