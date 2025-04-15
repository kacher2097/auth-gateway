package com.authenhub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GgSheetConfig {

    private final GoogleConfig googleConfig;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_FILE_PATH = "tokens.json";
    private static final File DATA_STORE_DIR = new File("tokens");

//    private Sheets getSheetService() throws IOException, GeneralSecurityException {
//        String credentialsPath = googleConfig.getCredential();
//        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(credentialsPath))
//                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/spreadsheets"));
//        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
//                .setApplicationName("Your App")
//                .build();
//    }

    public Credential getCredentials() throws IOException, GeneralSecurityException {
        // Load client secrets.
        InputStream in = new FileInputStream(googleConfig.getCredential());
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory)
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .build();

        // Kiểm tra và xóa token cũ nếu hết hạn
        Credential credential;
        try {
            credential = flow.loadCredential("user"); // Tải token hiện tại
            if (credential == null || !credential.refreshToken() || credential.getAccessToken() == null) {
                // Token không hợp lệ hoặc không thể làm mới, xóa và yêu cầu lại
                deleteStoredCredential();
                credential = authorizeNewFlow(flow);
            }
        } catch (TokenResponseException e) {
            if (e.getStatusCode() == 400 && "invalid_grant".equals(e.getDetails().get("error"))) {
                log.error("Token has expired or been revoked. Deleting and re-authorizing...");
                deleteStoredCredential();
                credential = authorizeNewFlow(flow);
                log.info("Token has expired -> Delete file and authorize again success");
            } else {
                log.error("Error refreshing token", e);
                throw e;
            }
        } catch (IOException e) {
            log.error("Reading file token error", e);
            deleteStoredCredential();
            credential = authorizeNewFlow(flow);
            log.info("Reading file token error -> delete file and authorize again success");
        }
        return credential;
    }

    private Credential authorizeNewFlow(GoogleAuthorizationCodeFlow flow) throws IOException {
        LocalServerReceiver localServerReceiver = new LocalServerReceiver.Builder().setPort(8091).build();
        return new AuthorizationCodeInstalledApp(flow, localServerReceiver)
                .authorize("user");
    }

    private void deleteStoredCredential() {
        File tokenFile = new File(DATA_STORE_DIR, "StoredCredential");
        if (tokenFile.exists()) {
            boolean deleted = tokenFile.delete();
            if (deleted) {
                log.debug("Deleted expired token file: {}", tokenFile.getAbsolutePath());
            } else {
                log.debug("Failed to delete token file: {}", tokenFile.getAbsolutePath());
            }
        }
    }

    public String getValidAccessToken() throws IOException, ParseException, GeneralSecurityException {
        JSONObject tokenData = loadTokenFromFile();
        String accessToken = (String) tokenData.get("access_token");
        String refreshToken = (String) tokenData.get("refresh_token");
        long expiresAt = (long) tokenData.get("expires_at");

        if (System.currentTimeMillis() < expiresAt) {
            return accessToken;
        } else {
            return refreshAccessToken(refreshToken);
        }
    }

    public String refreshAccessToken(String refreshToken) throws IOException, GeneralSecurityException {
        GoogleRefreshTokenRequest request = new GoogleRefreshTokenRequest(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                refreshToken,
                googleConfig.getClientId(),
                googleConfig.getClientSecret()
        );

        TokenResponse response = request.execute();
        String newAccessToken = response.getAccessToken();

        // Lưu lại token vào file
        saveTokenToFile(newAccessToken, refreshToken, response.getExpiresInSeconds());

        return newAccessToken;
    }

    public void saveTokenToFile(String accessToken, String refreshToken, long expiresIn) throws IOException {
        JSONObject tokenJson = new JSONObject();
        tokenJson.put("access_token", accessToken);
        tokenJson.put("refresh_token", refreshToken);
        tokenJson.put("expires_at", System.currentTimeMillis() + expiresIn * 1000);

        try (FileWriter file = new FileWriter("tokens.json")) {
            file.write(tokenJson.toString());
        }
    }

    public JSONObject loadTokenFromFile() throws IOException {
        // Đường dẫn đến file tokens.json
        Path filePath = Paths.get("tokens.json");

        // Kiểm tra file có tồn tại không
        if (!Files.exists(filePath)) {
            throw new IOException("File tokens.json not found at path: " + filePath.toAbsolutePath());
        }

        // Đọc file với mã hóa UTF-8
        String jsonContent;
        try {
            jsonContent = Files.readString(filePath);
        } catch (IOException e) {
            throw new IOException("Failed to read tokens.json: " + e.getMessage(), e);
        }

        // Parse JSON bằng Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Đọc JSON thành Map
            Map<String, Object> jsonMap = objectMapper.readValue(jsonContent, Map.class);
            // Chuyển Map thành JSONObject (org.json)
            return new JSONObject(jsonMap);
        } catch (Exception e) {
            throw new IOException("Failed to parse tokens.json as JSON object: " + e.getMessage(), e);
        }
    }

    public GoogleAuthorizationCodeFlow getFlow() throws IOException, GeneralSecurityException {
        InputStream in = new FileInputStream(DATA_STORE_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        List<String> scopes = Collections.singletonList("https://www.googleapis.com/auth/spreadsheets");
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();
    }

    public Credential authorize() throws IOException, GeneralSecurityException, ParseException {
        GoogleAuthorizationCodeFlow flow = getFlow();

        File tokenFile = new File(TOKENS_FILE_PATH);
        if (tokenFile.exists()) {
            return new Credential(BearerToken.authorizationHeaderAccessMethod())
                    .setAccessToken(getValidAccessToken());
        } else {
            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(8091)
                    .build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            saveTokenToFile(credential.getAccessToken(), credential.getRefreshToken(), credential.getExpiresInSeconds());
            return credential;
        }
    }

    @Bean
    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = getCredentials();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY, credential)
                .setApplicationName("Google sheet Integration with spring boot")
                .build();
    }
}
