package com.authenhub.bean.tool.dataextractor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataExtractorRequest {
    private String userId;
    private String extractionType; // web, file, text, database
    private String url;
    private String text;
    private String fileContent;
    private String fileType;
    private String databaseType;
    private String databaseUrl;
    private String databaseUsername;
    private String databasePassword;
    private String query;
    private List<String> dataTypes; // email, phone, address, name, company, social
}
