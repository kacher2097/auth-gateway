package com.authenhub.bean.tool.dataextractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractedDataItem {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String company;
    private String source;
    private String dataType;
    private String rawText;
    private Integer confidence;
}
