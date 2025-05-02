package com.authenhub.bean.tool.dataextractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataExtractorResponse {
    private List<ExtractedDataItem> items;
    private int totalCount;
    private Map<String, Integer> typeCounts;
    private String extractionDate;
    private String source;
    private String extractionTime;
}
