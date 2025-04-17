package com.authenhub.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common request DTO for pagination operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
