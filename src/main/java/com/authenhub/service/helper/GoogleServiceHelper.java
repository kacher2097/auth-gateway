package com.authenhub.service.helper;

//import com.google.api.services.drive.Drive;
//import com.google.api.services.gmail.Gmail;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for Google API operations using service account authentication.
 * This class demonstrates how to use the auto-refreshing credentials.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleServiceHelper {

    private final Sheets sheetsService;
//    private final Drive driveService;
//    private final Gmail gmailService;

    /**
     * Reads data from a Google Sheet
     *
     * @param spreadsheetId the ID of the spreadsheet
     * @param range the range to read (e.g., "Sheet1!A1:D10")
     * @return the values read from the sheet
     */
    public List<List<Object>> readSheet(String spreadsheetId, String range) {
        try {
            ValueRange response = sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();

            return response.getValues();
        } catch (IOException e) {
            log.error("Error reading from Google Sheet", e);
            return new ArrayList<>();
        }
    }

    /**
     * Writes data to a Google Sheet
     *
     * @param spreadsheetId the ID of the spreadsheet
     * @param range the range to write to (e.g., "Sheet1!A1")
     * @param values the values to write
     * @return number of updated cells
     */
    public int writeToSheet(String spreadsheetId, String range, List<List<Object>> values) {
        try {
            ValueRange body = new ValueRange()
                    .setValues(values);

            UpdateValuesResponse result = sheetsService.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();

            return result.getUpdatedCells();
        } catch (IOException e) {
            log.error("Error writing to Google Sheet", e);
            return 0;
        }
    }

    /**
     * Creates a new Google Sheet
     *
     * @param title the title of the new spreadsheet
     * @return the ID of the created spreadsheet
     */
    public String createSheet(String title) {
        try {
            Spreadsheet spreadsheet = new Spreadsheet()
                    .setProperties(new SpreadsheetProperties()
                            .setTitle(title));

            Spreadsheet response = sheetsService.spreadsheets().create(spreadsheet)
                    .execute();

            return response.getSpreadsheetId();
        } catch (IOException e) {
            log.error("Error creating Google Sheet", e);
            return null;
        }
    }

    /**
     * Lists files in Google Drive
     *
     * @param pageSize maximum number of files to return
     * @return list of file names and IDs
     */
//    public List<String> listDriveFiles(int pageSize) {
//        try {
//            List<String> fileInfo = new ArrayList<>();
//            Drive.Files.List request = driveService.files().list()
//                    .setPageSize(pageSize)
//                    .setFields("files(id, name)");
//
//            com.google.api.services.drive.model.FileList result = request.execute();
//            result.getFiles().forEach(file ->
//                fileInfo.add(String.format("%s (%s)", file.getName(), file.getId()))
//            );
//
//            return fileInfo;
//        } catch (IOException e) {
//            log.error("Error listing Drive files", e);
//            return new ArrayList<>();
//        }
//    }

    /**
     * Example method showing how to send an email using Gmail API
     * Note: This requires domain-wide delegation to be set up
     *
     * @param to recipient email address
     * @param subject email subject
     * @param bodyText email body text
     * @return message ID if successful, null otherwise
     */
//    public String sendEmail(String to, String subject, String bodyText) {
//        try {
//            // Create the email content
//            com.google.api.services.gmail.model.Message message = createEmail(to, subject, bodyText);
//
//            // Send the message
//            message = gmailService.users().messages().send("me", message).execute();
//
//            return message.getId();
//        } catch (Exception e) {
//            log.error("Error sending email", e);
//            return null;
//        }
//    }

    /**
     * Helper method to create an email message
     */
//    private com.google.api.services.gmail.model.Message createEmail(
//            String to, String subject, String bodyText) throws Exception {
//
//        // This is a simplified example - in a real application, you would use
//        // JavaMail API to create a proper MIME message
//        String email = "From: me\r\n" +
//                "To: " + to + "\r\n" +
//                "Subject: " + subject + "\r\n\r\n" +
//                bodyText;
//
//        // Encode as base64url string
//        byte[] bytes = email.getBytes();
//        String encodedEmail = java.util.Base64.getUrlEncoder().encodeToString(bytes);
//
//        com.google.api.services.gmail.model.Message message = new com.google.api.services.gmail.model.Message();
//        message.setRaw(encodedEmail);
//
//        return message;
//    }
}
