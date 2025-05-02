package com.authenhub.service.impl;

import com.authenhub.bean.tool.dataextractor.DataExtractorRequest;
import com.authenhub.bean.tool.dataextractor.DataExtractorResponse;
import com.authenhub.bean.tool.dataextractor.ExtractedDataItem;
import com.authenhub.entity.DataExtractorJob;
import com.authenhub.repository.DataExtractorJobRepository;
import com.authenhub.service.interfaces.IDataExtractorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import com.authenhub.utils.TimestampUtils;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataExtractorService implements IDataExtractorService {

    private final DataExtractorJobRepository dataExtractorJobRepository;
    private final ObjectMapper objectMapper;

    // Regular expressions for different data types
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+\\d{1,3}[- ]?)?\\d{3}[- ]?\\d{3}[- ]?\\d{4}");
    private static final Pattern NAME_PATTERN = Pattern.compile("([A-Z][a-z]+ [A-Z][a-z]+)");

    @Override
    public DataExtractorResponse extractData(DataExtractorRequest request) {
        log.info("Extracting data from source type: {}", request.getExtractionType());

        // Create a new job
        DataExtractorJob job = DataExtractorJob.builder()
                .userId(request.getUserId())
                .extractionType(request.getExtractionType())
                .url(request.getUrl())
                .text(request.getText())
                .fileContent(request.getFileContent())
                .fileType(request.getFileType())
                .databaseType(request.getDatabaseType())
                .databaseUrl(request.getDatabaseUrl())
                .databaseUsername(request.getDatabaseUsername())
                .dataTypes(request.getDataTypes() != null ? String.join(",", request.getDataTypes()) : null)
                .createdAt(TimestampUtils.now())
                .status("RUNNING")
                .build();

        job = dataExtractorJobRepository.save(job);

        try {
            String sourceText = "";

            // Get the source text based on the extraction type
            if ("web".equals(request.getExtractionType())) {
                // TODO: Implement web scraping logic
                sourceText = "Sample web content with email: john.doe@example.com and phone: +1 555-123-4567";
            } else if ("file".equals(request.getExtractionType())) {
                // TODO: Implement file parsing logic
                sourceText = request.getFileContent();
            } else if ("text".equals(request.getExtractionType())) {
                sourceText = request.getText();
            } else if ("database".equals(request.getExtractionType())) {
                // TODO: Implement database querying logic
                sourceText = "Sample database content with email: jane.smith@example.com and phone: +1 555-987-6543";
            }

            // Extract data from the source text
            List<ExtractedDataItem> items = new ArrayList<>();
            Map<String, Integer> typeCounts = new HashMap<>();

            // Extract data based on requested types
            if (request.getDataTypes() != null) {
                for (String dataType : request.getDataTypes()) {
                    List<ExtractedDataItem> extractedItems = extractDataByType(dataType, sourceText);
                    items.addAll(extractedItems);
                    typeCounts.put(dataType, extractedItems.size());
                }
            }

            DataExtractorResponse response = DataExtractorResponse.builder()
                    .items(items)
                    .totalCount(items.size())
                    .typeCounts(typeCounts)
                    .extractionDate(TimestampUtils.now().toString())
                    .source(request.getExtractionType())
                    .extractionTime("0.5 seconds")
                    .build();

            // Update job with results
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("COMPLETED");
            job.setTotalItems(items.size());
            job.setTypeCounts(objectMapper.writeValueAsString(typeCounts));
            job.setExtractionTime("0.5 seconds");
            job.setResultJson(objectMapper.writeValueAsString(response));
            dataExtractorJobRepository.save(job);

            return response;
        } catch (Exception e) {
            log.error("Error extracting data", e);

            // Update job with error
            job.setCompletedAt(TimestampUtils.now());
            job.setStatus("FAILED");
            job.setErrorMessage(e.getMessage());
            dataExtractorJobRepository.save(job);

            throw new RuntimeException("Failed to extract data: " + e.getMessage(), e);
        }
    }

    private List<ExtractedDataItem> extractDataByType(String dataType, String sourceText) {
        List<ExtractedDataItem> items = new ArrayList<>();

        if ("email".equals(dataType)) {
            Matcher matcher = EMAIL_PATTERN.matcher(sourceText);
            while (matcher.find()) {
                String email = matcher.group();
                items.add(ExtractedDataItem.builder()
                        .id(UUID.randomUUID().toString())
                        .email(email)
                        .dataType("email")
                        .source("text")
                        .rawText(email)
                        .confidence(95)
                        .build());
            }
        } else if ("phone".equals(dataType)) {
            Matcher matcher = PHONE_PATTERN.matcher(sourceText);
            while (matcher.find()) {
                String phone = matcher.group();
                items.add(ExtractedDataItem.builder()
                        .id(UUID.randomUUID().toString())
                        .phone(phone)
                        .dataType("phone")
                        .source("text")
                        .rawText(phone)
                        .confidence(90)
                        .build());
            }
        } else if ("name".equals(dataType)) {
            Matcher matcher = NAME_PATTERN.matcher(sourceText);
            while (matcher.find()) {
                String name = matcher.group();
                items.add(ExtractedDataItem.builder()
                        .id(UUID.randomUUID().toString())
                        .name(name)
                        .dataType("name")
                        .source("text")
                        .rawText(name)
                        .confidence(85)
                        .build());
            }
        }

        return items;
    }

    @Override
    public byte[] exportExtractedData(String format, DataExtractorResponse dataExtractorResponse) {
        log.info("Exporting extracted data to format: {}", format);

        // TODO: Implement actual export logic based on the format

        // For now, return mock data
        StringBuilder sb = new StringBuilder();

        if ("json".equalsIgnoreCase(format)) {
            sb.append("{\n");
            sb.append("  \"items\": [\n");
            for (int i = 0; i < dataExtractorResponse.getItems().size(); i++) {
                ExtractedDataItem item = dataExtractorResponse.getItems().get(i);
                sb.append("    {\n");
                sb.append("      \"id\": \"").append(item.getId()).append("\",\n");
                if (item.getName() != null) {
                    sb.append("      \"name\": \"").append(item.getName()).append("\",\n");
                }
                if (item.getEmail() != null) {
                    sb.append("      \"email\": \"").append(item.getEmail()).append("\",\n");
                }
                if (item.getPhone() != null) {
                    sb.append("      \"phone\": \"").append(item.getPhone()).append("\",\n");
                }
                sb.append("      \"dataType\": \"").append(item.getDataType()).append("\",\n");
                sb.append("      \"confidence\": ").append(item.getConfidence()).append("\n");
                sb.append("    }");
                if (i < dataExtractorResponse.getItems().size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append("  ],\n");
            sb.append("  \"totalCount\": ").append(dataExtractorResponse.getTotalCount()).append(",\n");
            sb.append("  \"extractionDate\": \"").append(dataExtractorResponse.getExtractionDate()).append("\",\n");
            sb.append("  \"source\": \"").append(dataExtractorResponse.getSource()).append("\"\n");
            sb.append("}");
        } else if ("csv".equalsIgnoreCase(format)) {
            sb.append("id,name,email,phone,dataType,confidence\n");
            for (ExtractedDataItem item : dataExtractorResponse.getItems()) {
                sb.append(item.getId()).append(",");
                sb.append("\"").append(item.getName() != null ? item.getName() : "").append("\",");
                sb.append("\"").append(item.getEmail() != null ? item.getEmail() : "").append("\",");
                sb.append("\"").append(item.getPhone() != null ? item.getPhone() : "").append("\",");
                sb.append("\"").append(item.getDataType()).append("\",");
                sb.append(item.getConfidence()).append("\n");
            }
        } else {
            // Default to plain text
            sb.append("Extracted Data\n");
            sb.append("Source: ").append(dataExtractorResponse.getSource()).append("\n");
            sb.append("Extraction Date: ").append(dataExtractorResponse.getExtractionDate()).append("\n\n");

            for (ExtractedDataItem item : dataExtractorResponse.getItems()) {
                sb.append("ID: ").append(item.getId()).append("\n");
                if (item.getName() != null) {
                    sb.append("Name: ").append(item.getName()).append("\n");
                }
                if (item.getEmail() != null) {
                    sb.append("Email: ").append(item.getEmail()).append("\n");
                }
                if (item.getPhone() != null) {
                    sb.append("Phone: ").append(item.getPhone()).append("\n");
                }
                sb.append("Data Type: ").append(item.getDataType()).append("\n");
                sb.append("Confidence: ").append(item.getConfidence()).append("%\n\n");
            }
        }

        return sb.toString().getBytes();
    }

    public List<DataExtractorJob> getUserJobs(String userId) {
        return dataExtractorJobRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public DataExtractorJob getJobById(Long jobId) {
        return dataExtractorJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Data extractor job not found with ID: " + jobId));
    }
}
