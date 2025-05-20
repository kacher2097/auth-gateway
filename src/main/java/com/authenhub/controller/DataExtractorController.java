package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.dataextractor.DataExtractorRequest;
import com.authenhub.bean.tool.dataextractor.DataExtractorResponse;
import com.authenhub.service.interfaces.IDataExtractorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tools/data-extractor")
@RequiredArgsConstructor
@Tag(name = "Data Extractor", description = "API để trích xuất dữ liệu từ các nguồn khác nhau")
public class DataExtractorController {

    private final IDataExtractorService dataExtractorService;

    /**
     * Extract data from a source
     *
     * @param request The data extractor request
     * @return The data extractor response
     */
    @PostMapping
    @Operation(summary = "Trích xuất dữ liệu",
            description = "Trích xuất dữ liệu từ một nguồn dựa trên các tham số đầu vào.")
    public ApiResponse<DataExtractorResponse> extractData(@RequestBody DataExtractorRequest request) {
        DataExtractorResponse response = dataExtractorService.extractData(request);
        return ApiResponse.success(response);
    }

    /**
     * Export extracted data to a file
     *
     * @param format   The export format (csv, json, excel)
     * @param response The data extractor response
     * @return The file content
     */
    @PostMapping("/export/{format}")
    @Operation(summary = "Xuất dữ liệu đã trích xuất ra file",
            description = "Xuất dữ liệu đã trích xuất ra file với các định dạng khác nhau (CSV, JSON, Excel).")
    public ApiResponse<byte[]> exportExtractedData(
            @PathVariable String format,
            @RequestBody DataExtractorResponse response) {

        byte[] fileContent = dataExtractorService.exportExtractedData(format, response);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(format));
        headers.setContentDispositionFormData("attachment", "extracted-data." + format);

        return ApiResponse.success(fileContent);
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
