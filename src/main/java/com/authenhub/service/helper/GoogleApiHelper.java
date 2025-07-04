package com.authenhub.service.helper;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleApiHelper {

    private final Sheets sheetService;

    //Create new spreadsheet on Google Sheets and set the name of the default sheet
    private Spreadsheet createSpreadsheet(Sheets service, String spreadsheetName)
            throws IOException {
        // Tạo spreadsheet mới chỉ với tiêu đề
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(spreadsheetName));
        Spreadsheet createdSpreadsheet = service.spreadsheets().create(spreadsheet).execute();
        String spreadsheetId = createdSpreadsheet.getSpreadsheetId();
        log.info("Spreadsheet created with ID: {}", spreadsheetId);

        // Thêm sheet với tên defaultSheetName
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setAddSheet(new AddSheetRequest()
                        .setProperties(new SheetProperties()
                                .setTitle("Sheet1"))));
        BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest()
                .setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, batchUpdateRequest).execute();

        // Lấy lại thông tin spreadsheet đầy đủ
        Spreadsheet updatedSpreadsheet = service.spreadsheets().get(spreadsheetId).execute();
        log.info("Sheet '{}' added to spreadsheet ID: {}", "Sheet1", spreadsheetId);

        return updatedSpreadsheet;
    }

    public void exportExcelToGoogle(String excelFileName) {
        try {
            Path filePath = Path.of(excelFileName);
            List<List<Object>> worksheetData = new ArrayList<>();
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
            }

            // Tạo hoặc sử dụng Spreadsheet ID có sẵn
            Spreadsheet spreadsheet = createSpreadsheet(sheetService, filePath.getFileName().toString());
            String spreadsheetId = spreadsheet.getSpreadsheetId();

            // Lấy sheetId từ spreadsheet
            int sheetId;
            if (spreadsheet.getSheets().isEmpty()) {
                // Nếu không có sheet, tạo sheet mới
                List<Request> createSheetRequests = new ArrayList<>();
                createSheetRequests.add(new Request()
                        .setAddSheet(new AddSheetRequest()
                                .setProperties(new SheetProperties()
                                        .setTitle("Sheet1"))));
                sheetService.spreadsheets().batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest()
                        .setRequests(createSheetRequests)).execute();

                // Lấy lại thông tin spreadsheet sau khi tạo sheet
                spreadsheet = sheetService.spreadsheets().get(spreadsheetId).execute();
            }
            sheetId = spreadsheet.getSheets().get(0).getProperties().getSheetId();
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
        } catch (Exception e) {
            log.error("Write excel to gg sheet have exception ", e);
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
