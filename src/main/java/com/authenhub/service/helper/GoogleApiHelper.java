package com.authenhub.service.helper;

//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.Permission;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.DimensionProperties;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.UpdateDimensionPropertiesRequest;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleApiHelper {

    private final Sheets sheetService;

    //Create new spreadsheet on Google Sheets (Google automatically creates a default "Sheet1")
    private Spreadsheet createSpreadsheet(Sheets service, String spreadsheetName)
            throws IOException {
        // Tạo spreadsheet mới với tiêu đề (Google tự động tạo sheet "Sheet1")
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(spreadsheetName));
        Spreadsheet createdSpreadsheet = service.spreadsheets().create(spreadsheet).execute();
        String spreadsheetId = createdSpreadsheet.getSpreadsheetId();
        log.info("Spreadsheet created with ID: {} (default Sheet1 automatically created)", spreadsheetId);

        // Lấy lại thông tin spreadsheet đầy đủ
        Spreadsheet updatedSpreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        log.info("Spreadsheet ready with default sheet: {}", updatedSpreadsheet.getSheets().get(0).getProperties().getTitle());

        return updatedSpreadsheet;
    }

    public String exportExcelToGoogle(String excelFileName) {
        try {
            log.info("Starting export Excel to Google Sheets for file: {}", excelFileName);
            Path filePath = Path.of(excelFileName);
            List<List<Object>> worksheetData = new ArrayList<>();

            // Read Excel file data
            try (InputStream is = new FileInputStream(filePath.toFile());
                 ReadableWorkbook workbook = new ReadableWorkbook(is)) {

                org.dhatim.fastexcel.reader.Sheet sheet = workbook.getFirstSheet();
                sheet.openStream().skip(1).forEach(row -> {
                    String index = row.getCellText(0);
                    String title = row.getCellAsString(1).orElse("");
                    String description = row.getCellAsString(2).orElse("");
                    String thumbnailImg = row.getCellAsString(3).orElse("");
                    String linkPostOriginal = row.getCellAsString(4).orElse("");
                    String content = row.getCellAsString(5).orElse("");
                    String listImg = row.getCellAsString(6).orElse("");

                    List<Object> rowData = new ArrayList<>();
                    rowData.add(index);
                    rowData.add(title);
                    rowData.add(description);
                    rowData.add(thumbnailImg);
                    rowData.add(linkPostOriginal);
                    rowData.add(content);
                    rowData.add(listImg);
                    worksheetData.add(rowData);
                });
                log.info("Successfully read {} rows from Excel file", worksheetData.size());
            } catch (Exception e) {
                log.error("Error reading Excel file: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to read Excel file: " + e.getMessage(), e);
            }

            // Create or use existing Spreadsheet with proper error handling
            Spreadsheet spreadsheet;
            String spreadsheetId;
            try {
                log.info("Creating Google Spreadsheet...");
                spreadsheet = createSpreadsheet(sheetService, filePath.getFileName().toString());
                spreadsheetId = spreadsheet.getSpreadsheetId();
                log.info("Successfully created spreadsheet with ID: {}", spreadsheetId);
            } catch (Exception e) {
                log.error("Failed to create Google Spreadsheet: {}", e.getMessage(), e);
                if (e.getMessage().contains("UserCredentials instance cannot refresh")) {
                    throw new RuntimeException("Google authentication failed: No refresh token available. Please reconfigure Google credentials with proper OAuth2 flow or use service account.", e);
                } else {
                    throw new RuntimeException("Failed to create Google Spreadsheet: " + e.getMessage(), e);
                }
            }

            // Lấy sheetId từ spreadsheet (Google đã tự động tạo sheet mặc định)
            int sheetId = spreadsheet.getSheets().get(0).getProperties().getSheetId();
            String sheetName = spreadsheet.getSheets().get(0).getProperties().getTitle();
            log.info("Using existing sheet: {} (ID: {})", sheetName, sheetId);
            //Đặt độ rộng cột (pixel)
            int[] columnWidths = {60, 150, 200, 150, 150, 500, 150};

            List<Request> requests = new ArrayList<>();
            requests.add(createUpdateCellsRequest(sheetId, worksheetData));
            requests.addAll(createColumnWidthRequests(sheetId, columnWidths));

            // Thực thi batch update
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                    .setRequests(requests);
            sheetService.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

            log.info("Cells updated {}", batchUpdateRequest.getResponseRanges());

            // Open Google spreadsheet in browser
            String url = "https://docs.google.com/spreadsheets/d/" + spreadsheetId;
            if (!GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()) {
                Desktop desk = Desktop.getDesktop();
                desk.browse(new URI(url));
            } else {
                log.info("Môi trường không hỗ trợ GUI, truy cập URL thủ công {}", url);
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    Runtime.getRuntime().exec("cmd /c start " + url);
                } else {
                    Runtime.getRuntime().exec("xdg-open " + url);
                }
            }
            log.info("Dữ liệu đã được đẩy lên Google Sheets thành công!");
            return url;
        } catch (Exception e) {
            log.error("Write excel to gg sheet have exception ", e);

            // Provide more specific error messages
            if (e.getMessage().contains("UserCredentials instance cannot refresh")) {
                throw new RuntimeException("Google authentication failed: No refresh token available. Please reconfigure Google credentials with proper OAuth2 flow or use service account.", e);
            } else if (e.getMessage().contains("invalid_grant")) {
                throw new RuntimeException("Google authentication failed: Token has expired or been revoked. Please re-authenticate.", e);
            } else if (e.getMessage().contains("403")) {
                throw new RuntimeException("Google Sheets API access denied. Please check your credentials and API permissions.", e);
            } else {
                throw new RuntimeException("Failed to export to Google Sheets: " + e.getMessage(), e);
            }
        }
    }

    // Tạo request để ghi dữ liệu và bật wrap text
    private Request createUpdateCellsRequest(int sheetId, List<List<Object>> values) {
        GridRange range = new GridRange()
                .setSheetId(sheetId)
                .setStartRowIndex(0)
                .setEndRowIndex(values.size())
                .setStartColumnIndex(0)
                .setEndColumnIndex(7);

        List<RowData> rowDataList = values.stream()
                .map(rowData -> new RowData().setValues(
                        rowData.stream()
                                .map(cellData -> new CellData()
                                        .setUserEnteredValue(new ExtendedValue()
                                                .setStringValue(String.valueOf(cellData))
                                        )
                                        .setUserEnteredFormat(new CellFormat()
                                                .setWrapStrategy("WRAP")
                                                .setVerticalAlignment("MIDDLE")
                                        )
                                ).toList()
                )).toList();

        UpdateCellsRequest updateCells = new UpdateCellsRequest()
                .setRange(range)
                .setRows(rowDataList)
                .setFields("userEnteredValue,userEnteredFormat.wrapStrategy,userEnteredFormat.verticalAlignment");

        return new Request().setUpdateCells(updateCells);
    }

    // Tạo danh sách requests để đặt độ rộng cột
    private List<Request> createColumnWidthRequests(int sheetId, int[] columnWidths) {
        List<Request> requests = new ArrayList<>();
        for (int i = 0; i < columnWidths.length; i++) {
            DimensionRange columnRange = new DimensionRange()
                    .setSheetId(sheetId)
                    .setDimension("COLUMNS")
                    .setStartIndex(i)
                    .setEndIndex(i + 1);

            DimensionProperties properties = new DimensionProperties()
                    .setPixelSize(columnWidths[i]);

            UpdateDimensionPropertiesRequest updateDimension = new UpdateDimensionPropertiesRequest()
                    .setRange(columnRange)
                    .setProperties(properties)
                    .setFields("pixelSize");

            requests.add(new Request().setUpdateDimensionProperties(updateDimension));
        }
        return requests;
    }
}
