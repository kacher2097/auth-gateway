package com.authenhub.bean.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private List<T> content;

    /**
     * Create a PagedResponse from a list of items and pagination information
     * 
     * @param content the list of items
     * @param page the current page (0-based)
     * @param size the page size
     * @param totalElements the total number of elements
     * @return a PagedResponse
     */
    public static <T> PagedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return PagedResponse.<T>builder()
                .totalPages(totalPages)
                .totalElements(totalElements)
                .content(content)
                .page(page)
                .size(size)
                .build();
    }
}
