package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.dataextractor.DataExtractorRequest;
import com.authenhub.bean.tool.dataextractor.DataExtractorResponse;
import com.authenhub.service.interfaces.IDataExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools/data-extractor")
@RequiredArgsConstructor
public class DataExtractorController {

    private final IDataExtractorService dataExtractorService;

    /**
     * Extract data from a source
     * @param request The data extractor request
     * @return The data extractor response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DataExtractorResponse>> extractData(@RequestBody DataExtractorRequest request) {
        DataExtractorResponse response = dataExtractorService.extractData(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Export extracted data to a file
     * @param format The export format (csv, json, excel)
     * @param response The data extractor response
     * @return The file content
     */
    @PostMapping("/export/{format}")
    public ResponseEntity<byte[]> exportExtractedData(
            @PathVariable String format,
            @RequestBody DataExtractorResponse response) {
        
        byte[] fileContent = dataExtractorService.exportExtractedData(format, response);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "extracted-data." + format);
        
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    
    private MediaType getMediaType(String format) {
        switch (format.toLowerCase()) {
            case "json":
                return MediaType.APPLICATION_JSON;
            case "csv":
                return MediaType.parseMediaType("text/csv");
            case "excel":
                return MediaType.parseMediaType("application/vnd.ms-excel");
            default:
                return MediaType.TEXT_PLAIN;
        }
    }
}
