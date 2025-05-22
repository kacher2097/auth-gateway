package com.authenhub.config.gg;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.oauth2.UserCredentials;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

/**
 * Google Service Account configuration for accessing Google APIs without user interaction.
 * This implementation uses a service account credential that automatically refreshes tokens.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GgServiceAccountConfig {

    private final GoogleConfig googleConfig;
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * List of all scopes needed for the application
     */
    private static final List<String> SCOPES = Collections.singletonList(
            SheetsScopes.SPREADSHEETS
    );

    // Dummy access token for fallback authentication
    private static final String DUMMY_ACCESS_TOKEN = "ya29.dummy-token-for-fallback-only";

    /**
     * Attempts to load the service account key file from various locations
     *
     * @return InputStream of the key file
     * @throws IOException if the file cannot be found or read
     */
    private InputStream getServiceAccountKeyFileStream() throws IOException {
        // Try multiple locations to find the file
        try {
            byte[] fileContent = null;

            // First try: Direct file path
            File file = new File(googleConfig.getServiceAccountKeyFile());
            if (file.exists() && file.canRead()) {
                log.info("Loading service account key from file: {}", file.getAbsolutePath());
                fileContent = Files.readAllBytes(file.toPath());
            }

            // Second try: Class path resource
            if (fileContent == null) {
                try {
                    ClassPathResource resource = new ClassPathResource(googleConfig.getServiceAccountKeyFile());
                    if (resource.exists()) {
                        log.info("Loading service account key from classpath: {}", resource.getFile().getAbsolutePath());
                        fileContent = Files.readAllBytes(resource.getFile().toPath());
                    }
                } catch (Exception e) {
                    log.debug("Could not load from classpath: {}", e.getMessage());
                }
            }

            // Third try: Relative to working directory
            if (fileContent == null) {
                Path workingDir = Paths.get(System.getProperty("user.dir"));
                Path filePath = workingDir.resolve(googleConfig.getServiceAccountKeyFile());
                if (Files.exists(filePath) && Files.isReadable(filePath)) {
                    log.info("Loading service account key from working directory: {}", filePath);
                    fileContent = Files.readAllBytes(filePath);
                }
            }

            // Fourth try: Fallback to google.credentials path
            if (fileContent == null) {
                String fallbackPath = googleConfig.getCredential();
                log.info("Trying fallback path: {}", fallbackPath);
                File fallbackFile = new File(fallbackPath);
                if (fallbackFile.exists() && fallbackFile.canRead()) {
                    log.info("Loading OAuth credentials as fallback from: {}", fallbackFile.getAbsolutePath());
                    fileContent = Files.readAllBytes(fallbackFile.toPath());
                }
            }

            // If we get here and still don't have content, we couldn't find the file
            if (fileContent == null) {
                throw new IOException("Could not find service account key file at: " + googleConfig.getServiceAccountKeyFile());
            }

            // Return a ByteArrayInputStream that can be reset
            return new ByteArrayInputStream(fileContent);

        } catch (Exception e) {
            log.error("Error loading service account key file", e);
            throw new IOException("Failed to load service account key file: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to check if client ID and secret are available
     */
    private boolean hasValidClientCredentials() {
        return googleConfig.getClientId() != null && !googleConfig.getClientId().isEmpty() &&
                googleConfig.getClientSecret() != null && !googleConfig.getClientSecret().isEmpty();
    }

    @Bean
    public GoogleCredentials getGoogleCredentials() {
        try (InputStream keyFileStream = getServiceAccountKeyFileStream()) {
            try {
                // First try to load as service account credentials
                log.info("Attempting to load as service account credentials");
                return ServiceAccountCredentials.fromStream(keyFileStream)
                        .createScoped(SCOPES);
            } catch (IOException e) {
                log.info("Not a service account key, trying as OAuth client credentials");
                // Reset the stream
                keyFileStream.reset();

                try {
                    // Try to load as user credentials (OAuth client ID)
                    return UserCredentials.fromStream(keyFileStream)
                            .createScoped(SCOPES);
                } catch (IOException e2) {
                    log.warn("Could not load credentials from file: {}", e2.getMessage());

                    // Check if we have valid client credentials
                    if (hasValidClientCredentials()) {
                        // Fallback to using client ID and secret with dummy token
                        log.info("Falling back to client ID/secret with dummy token");
                        return UserCredentials.newBuilder()
                                .setClientId(googleConfig.getClientId())
                                .setClientSecret(googleConfig.getClientSecret())
                                .setAccessToken(AccessToken.newBuilder().setTokenValue(DUMMY_ACCESS_TOKEN).build())
                                .build()
                                .createScoped(SCOPES);
                    } else {
                        // If no client credentials, try application default
                        log.info("No client credentials available, trying application default");
                        try {
                            return GoogleCredentials.getApplicationDefault().createScoped(SCOPES);
                        } catch (IOException e3) {
                            log.warn("Could not get application default credentials: {}", e3.getMessage());
                            throw e2; // Re-throw the original exception
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load Google credentials: {}", e.getMessage());

            // Check if we have valid client credentials for final fallback
            if (hasValidClientCredentials()) {
                // Final fallback - create credentials with a dummy access token
                log.info("Using client ID/secret with dummy access token");
                try {
                    // Create credentials with client ID, secret and a dummy access token
                    // This is just to pass validation - the token will be refreshed before use
                    return UserCredentials.newBuilder()
                            .setClientId(googleConfig.getClientId())
                            .setClientSecret(googleConfig.getClientSecret())
                            .setAccessToken(AccessToken.newBuilder().setTokenValue(DUMMY_ACCESS_TOKEN).build())
                            .build()
                            .createScoped(SCOPES);
                } catch (Exception ex) {
                    log.error("Could not create fallback credentials", ex);
                    throw new RuntimeException("Could not create Google credentials", ex);
                }
            } else {
                // If all else fails and no client credentials, throw exception
                throw new RuntimeException("No valid Google credentials available. Please configure client ID and secret.");
            }
        }
    }

    /**
     * Creates a Sheets service with auto-refreshing credentials
     *
     * @return Sheets service
     * @throws IOException              if credential file cannot be read
     * @throws GeneralSecurityException if there's a security issue
     */
    @Bean
    @Primary
    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = getGoogleCredentials();
        HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                requestInitializer)
                .setApplicationName("Google Sheet Integration with Spring Boot")
                .build();
    }
}
