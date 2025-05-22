package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.config.gg.GoogleConfig;
import com.authenhub.service.helper.GoogleServiceHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Google API operations
 * This demonstrates how to use the GoogleServiceHelper with auto-refreshing credentials
 */
@Slf4j
@RestController
@RequestMapping("/api/google")
@RequiredArgsConstructor
@Tag(name = "Google API", description = "API để tương tác với các dịch vụ của Google như Sheets, Drive, Gmail")
public class GoogleApiController {

    private final GoogleServiceHelper googleServiceHelper;
    private final GoogleConfig googleConfig;

    /**
     * Read data from a Google Sheet
     *
     * @param range the range to read (e.g., "Sheet1!A1:D10")
     * @return the values read from the sheet
     */
//    @GetMapping("/sheets/read")
//    @Operation(summary = "Đọc dữ liệu từ Google Sheet",
//            description = "Đọc dữ liệu từ một bảng tính Google Sheets trong một phạm vi cụ thể.")
//    public ApiResponse<?> readSheet(
//            @RequestParam(required = false) String spreadsheetId,
//            @RequestParam String range) {
//
//        // Use the configured spreadsheet ID if none is provided
//        String sheetId = spreadsheetId != null ? spreadsheetId : googleConfig.get();
//
//        List<List<Object>> data = googleServiceHelper.readSheet(sheetId, range);
//        return ApiResponse.success(data);
//    }
//
//    /**
//     * Write data to a Google Sheet
//     *
//     * @param spreadsheetId the ID of the spreadsheet (optional)
//     * @param range the range to write to (e.g., "Sheet1!A1")
//     * @param values the values to write
//     * @return number of updated cells
//     */
//    @PostMapping("/sheets/write")
//    @Operation(summary = "Ghi dữ liệu vào Google Sheet",
//            description = "Ghi dữ liệu vào một bảng tính Google Sheets trong một phạm vi cụ thể.")
//    public ApiResponse<?> writeToSheet(
//            @RequestParam(required = false) String spreadsheetId,
//            @RequestParam String range,
//            @RequestBody List<List<Object>> values) {
//
//        // Use the configured spreadsheet ID if none is provided
//        String sheetId = spreadsheetId != null ? spreadsheetId : googleConfig.getSpreadsheetIdNew();
//
//        int updatedCells = googleServiceHelper.writeToSheet(sheetId, range, values);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("updatedCells", updatedCells);
//        response.put("spreadsheetId", sheetId);
//        response.put("range", range);
//
//        return ApiResponse.success(response);
//    }

    /**
     * Create a new Google Sheet
     *
     * @param title the title of the new spreadsheet
     * @return the ID of the created spreadsheet
     */
    @PostMapping("/sheets/create")
    @Operation(summary = "Tạo Google Sheet mới",
            description = "Tạo một bảng tính Google Sheets mới với tiêu đề được chỉ định.")
    public ApiResponse<?> createSheet(@RequestParam String title) {
        String spreadsheetId = googleServiceHelper.createSheet(title);

        Map<String, String> response = new HashMap<>();
        response.put("spreadsheetId", spreadsheetId);
        response.put("title", title);
        response.put("url", "https://docs.google.com/spreadsheets/d/" + spreadsheetId);

        return ApiResponse.success(response);
    }

    /**
     * List files in Google Drive
     *
     * @param pageSize maximum number of files to return
     * @return list of file names and IDs
     */
//    @GetMapping("/drive/files")
//    public ResponseEntity<List<String>> listDriveFiles(
//            @RequestParam(defaultValue = "10") int pageSize) {
//
//        List<String> files = googleServiceHelper.listDriveFiles(pageSize);
//        return ResponseEntity.ok(files);
//    }

    /**
     * Send an email using Gmail API
     * Note: This requires domain-wide delegation to be set up
     *
     * @param to recipient email address
     * @param subject email subject
     * @param body email body text
     * @return message ID if successful
     */
//    @PostMapping("/gmail/send")
//    public ResponseEntity<Map<String, String>> sendEmail(
//            @RequestParam String to,
//            @RequestParam String subject,
//            @RequestParam String body) {
//
//        String messageId = googleServiceHelper.sendEmail(to, subject, body);
//
//        Map<String, String> response = new HashMap<>();
//        response.put("messageId", messageId);
//        response.put("status", messageId != null ? "sent" : "failed");
//
//        return ResponseEntity.ok(response);
//    }
}
