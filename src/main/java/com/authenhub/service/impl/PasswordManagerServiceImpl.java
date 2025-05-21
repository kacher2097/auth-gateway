package com.authenhub.service.impl;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.common.PagedResponse;
import com.authenhub.bean.passwordmanager.CheckPasswordStrengthRequest;
import com.authenhub.bean.passwordmanager.CheckPasswordStrengthResponse;
import com.authenhub.bean.passwordmanager.ExportPasswordsResponse;
import com.authenhub.bean.passwordmanager.GeneratePasswordRequest;
import com.authenhub.bean.passwordmanager.GeneratePasswordResponse;
import com.authenhub.bean.passwordmanager.ImportPasswordsRequest;
import com.authenhub.bean.passwordmanager.ImportPasswordsResponse;
import com.authenhub.bean.passwordmanager.PasswordCreateRequest;
import com.authenhub.bean.passwordmanager.PasswordResponse;
import com.authenhub.bean.passwordmanager.PasswordUpdateRequest;
import com.authenhub.bean.passwordmanager.PwManagerSearchReq;
import com.authenhub.constant.enums.ApiResponseCode;
import com.authenhub.entity.PasswordManage;
import com.authenhub.entity.PasswordManage_;
import com.authenhub.repository.PasswordManageRepository;
import com.authenhub.service.PasswordManagerService;
import com.authenhub.utils.PasswordEncryptionUtil;
import com.authenhub.utils.PasswordUtils;
import com.authenhub.utils.SpecificationCustom;
import com.authenhub.utils.TimestampUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordManagerServiceImpl implements PasswordManagerService {

    private final PasswordManageRepository passwordManageRepository;
    private final PasswordEncryptionUtil passwordEncryptionUtil;
    private final PasswordUtils passwordUtils;

    @Override
    public ApiResponse<?> searchPassword(PwManagerSearchReq request) {
        log.info("Searching password with request {}", request);

        // Set default values for pagination if not provided
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 10;

        // Validate and set default values for sorting
        String sortBy = validateSortField(request.getSortBy());
        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortDirection()) ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        // Create pageable object with sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Create specification for filtering
        Specification<PasswordManage> spec = buildSpecification(request);

        // Execute query with pagination
        Page<PasswordManage> passwordPage = passwordManageRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<PasswordResponse> responses = passwordPage.getContent().stream()
                .map(PasswordResponse::fromEntity)
                .collect(Collectors.toList());

        // Create paged response
        PagedResponse<PasswordResponse> pagedResponse = PagedResponse.of(
                responses,
                passwordPage.getNumber(),
                passwordPage.getSize(),
                passwordPage.getTotalElements()
        );

        return ApiResponse.success(pagedResponse);
    }

    private String validateSortField(String sortBy) {
        if (sortBy == null) {
            return "createdAt";
        }

        // List of allowed sort fields
        List<String> allowedFields = Arrays.asList(
                "id", "siteUrl", "username", "createdAt", "updatedAt");

        return allowedFields.contains(sortBy) ? sortBy : "createdAt";
    }

    @Override
    public ApiResponse<PasswordResponse> getPasswordById(Long id, boolean includePassword) {
        log.info("Getting password by ID: {}, includePassword: {}", id, includePassword);

        Optional<PasswordManage> passwordManageOpt = passwordManageRepository.findById(id);

        if (passwordManageOpt.isEmpty()) {
            return ApiResponse.error("404", "Password not found");
        }

        PasswordManage passwordManage = passwordManageOpt.get();
        PasswordResponse response;

        if (includePassword) {
            // Decrypt the password
            String decryptedPassword = passwordEncryptionUtil.decrypt(passwordManage.getEncryptedPassword());
            response = PasswordResponse.fromEntityWithPassword(passwordManage, decryptedPassword);
        } else {
            response = PasswordResponse.fromEntity(passwordManage);
        }

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<PasswordResponse> createPassword(PasswordCreateRequest request) {
        log.info("Creating password for site: {}, username: {}", request.getSiteUrl(), request.getUsername());

        // Check if a password entry already exists for this site and username
        Optional<PasswordManage> existingPasswordOpt = passwordManageRepository.findBySiteUrlAndUsername(
                request.getSiteUrl(), request.getUsername());

        if (existingPasswordOpt.isPresent()) {
            return ApiResponse.error("409", "A password entry already exists for this site and username");
        }

        // Encrypt the password
        String encryptedPassword = passwordEncryptionUtil.encrypt(request.getPassword());

        // Create a new password entry
        PasswordManage passwordManage = PasswordManage.builder()
                .siteUrl(request.getSiteUrl())
                .provider(request.getProvider())
                .username(request.getUsername())
                .encryptedPassword(encryptedPassword)
                .iconUrl(request.getIconUrl())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();

        // Save the password entry
        passwordManage = passwordManageRepository.save(passwordManage);

        // Return the response without the decrypted password
        PasswordResponse response = PasswordResponse.fromEntity(passwordManage);

        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<PasswordResponse> updatePassword(Long id, PasswordUpdateRequest request) {
        log.info("Updating password with ID: {}", id);

        Optional<PasswordManage> passwordManageOpt = passwordManageRepository.findById(id);

        if (passwordManageOpt.isEmpty()) {
            return ApiResponse.error("404", "Password not found");
        }

        PasswordManage passwordManage = passwordManageOpt.get();

        // Update the fields if provided
        if (StringUtils.isNotBlank(request.getSiteUrl())) {
            passwordManage.setSiteUrl(request.getSiteUrl());
        }

        if (StringUtils.isNotBlank(request.getUsername())) {
            passwordManage.setUsername(request.getUsername());
        }

        if (StringUtils.isNotBlank(request.getPassword())) {
            // Encrypt the new password
            String encryptedPassword = passwordEncryptionUtil.encrypt(request.getPassword());
            passwordManage.setEncryptedPassword(encryptedPassword);
        }

        if (request.getIconUrl() != null) {
            passwordManage.setIconUrl(request.getIconUrl());
        }

        passwordManage.setUpdatedAt(TimestampUtils.now());
        passwordManage = passwordManageRepository.save(passwordManage);

        PasswordResponse response = PasswordResponse.fromEntity(passwordManage);
        return ApiResponse.success(response);
    }

    @Override
    public ApiResponse<?> deletePassword(Long id) {
        log.info("Deleting password with ID: {}", id);

        Optional<PasswordManage> passwordManageOpt = passwordManageRepository.findById(id);

        if (passwordManageOpt.isEmpty()) {
            return ApiResponse.error("404", "Password not found");
        }

        PasswordManage passwordManage = passwordManageOpt.get();

        // Delete the password entry
        passwordManageRepository.delete(passwordManage);
        return ApiResponse.success(ApiResponseCode.SUCCESS_CODE);
    }

    @Override
    public ApiResponse<GeneratePasswordResponse> generatePassword(GeneratePasswordRequest request) {
        log.info("Generating random password with length: {}", request.getLength());

        try {
            String password = passwordUtils.generateRandomPassword(
                    request.getLength(),
                    request.isIncludeLowercase(),
                    request.isIncludeUppercase(),
                    request.isIncludeNumbers(),
                    request.isIncludeSpecial()
            );

            GeneratePasswordResponse response = new GeneratePasswordResponse(password);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.error("Error generating password: {}", e.getMessage());
            return ApiResponse.error("400", e.getMessage());
        }
    }

    @Override
    public ApiResponse<CheckPasswordStrengthResponse> checkPasswordStrength(CheckPasswordStrengthRequest request) {
        log.info("Checking password strength");

        PasswordUtils.PasswordStrength strength = passwordUtils.checkPasswordStrength(request.getPassword());

        CheckPasswordStrengthResponse response = CheckPasswordStrengthResponse.builder()
                .score(strength.getScore())
                .strength(strength.getStrength())
                .feedback(strength.getFeedback())
                .build();

        return ApiResponse.success(response);
    }

    @Override
    @PreAuthorize("hasAuthority('password:export')")
    public ApiResponse<ExportPasswordsResponse> exportPasswords(String format, boolean includePasswords) {
        log.info("Exporting passwords in format: {}, includePasswords: {}", format, includePasswords);

        List<PasswordManage> passwordManages = passwordManageRepository.findAll();
        List<ExportPasswordsResponse.PasswordExportItem> exportItems = new ArrayList<>();

        for (PasswordManage passwordManage : passwordManages) {
            ExportPasswordsResponse.PasswordExportItem item = ExportPasswordsResponse.PasswordExportItem.builder()
                    .id(passwordManage.getId())
                    .siteUrl(passwordManage.getSiteUrl())
                    .username(passwordManage.getUsername())
                    .iconUrl(passwordManage.getIconUrl())
                    .build();

            if (includePasswords) {
                // Decrypt the password
                String decryptedPassword = passwordEncryptionUtil.decrypt(passwordManage.getEncryptedPassword());
                item.setPassword(decryptedPassword);
            }

            exportItems.add(item);
        }

        String exportedData;
        if ("CSV".equalsIgnoreCase(format)) {
            exportedData = exportToCsv(exportItems, includePasswords);
        } else {
            // Default to JSON
            exportedData = exportToJson(exportItems);
        }

        ExportPasswordsResponse response = ExportPasswordsResponse.builder()
                .passwords(exportItems)
                .format(format.toUpperCase())
                .exportedData(exportedData)
                .build();

        return ApiResponse.success(response);
    }

    @Override
//    @PreAuthorize("hasAuthority('password:import')")
    public ApiResponse<ImportPasswordsResponse> importPasswords(ImportPasswordsRequest request) {
        log.info("Importing passwords from format: {}", request.getFormat());

        List<ImportPasswordsRequest.PasswordImportItem> importItems;

        if (request.getPasswords() != null && !request.getPasswords().isEmpty()) {
            // Use the provided password items
            importItems = request.getPasswords();
        } else if (StringUtils.isNotBlank(request.getImportData())) {
            // Parse the import data
            if ("CSV".equalsIgnoreCase(request.getFormat())) {
                importItems = importFromCsv(request.getImportData());
            } else {
                // Default to JSON
                importItems = importFromJson(request.getImportData());
            }
        } else {
            return ApiResponse.error("400", "No password data provided");
        }

        int totalImported = 0;
        int totalSkipped = 0;
        int totalFailed = 0;

        for (ImportPasswordsRequest.PasswordImportItem item : importItems) {
            try {
                // Validate required fields
                if (StringUtils.isBlank(item.getSiteUrl()) || StringUtils.isBlank(item.getUsername()) ||
                        StringUtils.isBlank(item.getPassword())) {
                    log.warn("Skipping import item due to missing required fields: {}", item);
                    totalSkipped++;
                    continue;
                }

                // Check if a password entry already exists for this site and username
                Optional<PasswordManage> existingPasswordOpt = passwordManageRepository.findBySiteUrlAndUsername(
                        item.getSiteUrl(), item.getUsername());

                if (existingPasswordOpt.isPresent()) {
                    log.info("Password entry already exists for site: {}, username: {}",
                            item.getSiteUrl(), item.getUsername());
                    totalSkipped++;
                    continue;
                }

                // Encrypt the password
                String encryptedPassword = passwordEncryptionUtil.encrypt(item.getPassword());

                // Create a new password entry
                PasswordManage passwordManage = PasswordManage.builder()
                        .siteUrl(item.getSiteUrl())
                        .username(item.getUsername())
                        .encryptedPassword(encryptedPassword)
                        .iconUrl(item.getIconUrl())
                        .createdAt(TimestampUtils.now())
                        .updatedAt(TimestampUtils.now())
                        .build();

                // Save the password entry
                passwordManageRepository.save(passwordManage);
                totalImported++;
            } catch (Exception e) {
                log.error("Error importing password entry: {}", e.getMessage(), e);
                totalFailed++;
            }
        }

        ImportPasswordsResponse response = ImportPasswordsResponse.builder()
                .totalImported(totalImported)
                .totalSkipped(totalSkipped)
                .totalFailed(totalFailed)
                .message(String.format("Imported %d passwords, skipped %d, failed %d",
                        totalImported, totalSkipped, totalFailed))
                .build();

        return ApiResponse.success(response);
    }

    // Helper methods for import/export

    private String exportToCsv(List<ExportPasswordsResponse.PasswordExportItem> items, boolean includePasswords) {
        StringBuilder csv = new StringBuilder();

        // Add header
        if (includePasswords) {
            csv.append("ID,Site URL,Username,Password,Icon URL\n");
        } else {
            csv.append("ID,Site URL,Username,Icon URL\n");
        }

        // Add data rows
        for (ExportPasswordsResponse.PasswordExportItem item : items) {
            csv.append(item.getId()).append(",");
            csv.append(escapeCsvField(item.getSiteUrl())).append(",");
            csv.append(escapeCsvField(item.getUsername())).append(",");

            if (includePasswords) {
                csv.append(escapeCsvField(item.getPassword())).append(",");
            }

            csv.append(escapeCsvField(item.getIconUrl())).append("\n");
        }

        return csv.toString();
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        // Escape quotes and wrap in quotes if the field contains commas, quotes, or newlines
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    private String exportToJson(List<ExportPasswordsResponse.PasswordExportItem> items) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(items);
        } catch (IOException e) {
            log.error("Error exporting to JSON: {}", e.getMessage(), e);
            return "[]";
        }
    }

    private List<ImportPasswordsRequest.PasswordImportItem> importFromCsv(String csvData) {
        List<ImportPasswordsRequest.PasswordImportItem> items = new ArrayList<>();

        String[] lines = csvData.split("\n");
        if (lines.length <= 1) {
            // Only header or empty file
            return items;
        }

        // Skip header line
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            // Parse CSV line (simple implementation, doesn't handle all CSV edge cases)
            String[] fields = parseCsvLine(line);

            if (fields.length < 3) {
                // Not enough fields
                continue;
            }

            ImportPasswordsRequest.PasswordImportItem item = new ImportPasswordsRequest.PasswordImportItem();

            // Skip ID field (fields[0])
            item.setSiteUrl(fields[1]);
            item.setUsername(fields[2]);

            if (fields.length > 3) {
                item.setPassword(fields[3]);
            }

            if (fields.length > 4) {
                item.setIconUrl(fields[4]);
            }

            items.add(item);
        }

        return items;
    }

    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++; // Skip the next quote
                } else {
                    // Toggle quote mode
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // End of field
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }

    private List<ImportPasswordsRequest.PasswordImportItem> importFromJson(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return Arrays.asList(objectMapper.readValue(jsonData, ImportPasswordsRequest.PasswordImportItem[].class));
        } catch (IOException e) {
            log.error("Error importing from JSON: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    private Specification<PasswordManage> buildSpecification(PwManagerSearchReq request) {
        Specification<PasswordManage> specKeyword = SpecificationCustom.hasLikeIgnoreCase(PasswordManage_.SITE_URL, request.getKeyword());
        Specification<PasswordManage> specUsername = SpecificationCustom.hasLikeIgnoreCase(PasswordManage_.USERNAME, request.getKeyword());
        return SpecificationCustom.and(specKeyword, specUsername);
    }
}
