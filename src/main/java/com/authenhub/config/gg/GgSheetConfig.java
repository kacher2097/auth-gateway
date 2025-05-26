package com.authenhub.config.gg;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GgSheetConfig {

    private static final String APPLICATION_NAME = "Google Sheet Integration with Spring Boot";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleConfig googleConfig;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        String credentialsPath = googleConfig.getCredential();
        String absolutePath = Paths.get(credentialsPath).toAbsolutePath().toString();

        log.info("Loading Google credentials from: {}", absolutePath);

        // Check if file exists before attempting to load
        if (!Paths.get(absolutePath).toFile().exists()) {
            String errorMessage = String.format(
                "Google credentials file not found at: %s. " +
                "Please ensure the file exists and contains valid service account credentials. " +
                "You can download this file from Google Cloud Console > IAM & Admin > Service Accounts.",
                absolutePath
            );
            log.error(errorMessage);
            throw new IOException(errorMessage);
        }

        try (FileInputStream in = new FileInputStream(absolutePath)) {
            List<String> scopes = googleConfig.getScopes() != null && !googleConfig.getScopes().isEmpty()
                    ? googleConfig.getScopes()
                    : Collections.singletonList(SheetsScopes.SPREADSHEETS);
            log.debug("Using scopes: {}", scopes);

            GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                    .createScoped(scopes);
            // Tự động làm mới token nếu cần
            credentials.refreshIfExpired();
            log.info("Successfully loaded Google credentials with scopes: {}", scopes);
            return credentials;
        } catch (IOException e) {
            log.error("Failed to load Google credentials from: {}", absolutePath, e);
            throw new IOException("Cannot load credentials from " + absolutePath + ". Ensure the file exists and is valid.", e);
        }
    }

    @Bean
    public Sheets sheetsService(GoogleCredentials credentials) throws GeneralSecurityException, IOException {
        log.info("Initializing Google Sheets service");
        try {
            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (GeneralSecurityException e) {
            log.error("Failed to initialize Sheets service", e);
            throw e;
        }
    }
}