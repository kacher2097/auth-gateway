package com.authenhub.service;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.passwordmanager.*;
import com.authenhub.utils.PasswordUtils;

import java.util.List;

public interface PasswordManagerService {
    /**
     * Search for password entries based on criteria
     *
     * @param request search criteria
     * @return API response with search results
     */
    ApiResponse<?> searchPassword(PwManagerSearchReq request);

    /**
     * Get a password entry by ID
     *
     * @param id password entry ID
     * @param includePassword whether to include the decrypted password
     * @return API response with the password entry
     */
    ApiResponse<PasswordResponse> getPasswordById(Long id, boolean includePassword);

    /**
     * Create a new password entry
     *
     * @param request password creation request
     * @return API response with the created password entry
     */
    ApiResponse<PasswordResponse> createPassword(PasswordCreateRequest request);

    /**
     * Update an existing password entry
     *
     * @param id password entry ID
     * @param request password update request
     * @return API response with the updated password entry
     */
    ApiResponse<PasswordResponse> updatePassword(Long id, PasswordUpdateRequest request);

    /**
     * Delete a password entry
     *
     * @param id password entry ID
     * @return API response indicating success
     */
    ApiResponse<?> deletePassword(Long id);

    /**
     * Generate a random password
     *
     * @param request password generation request
     * @return API response with the generated password
     */
    ApiResponse<GeneratePasswordResponse> generatePassword(GeneratePasswordRequest request);

    /**
     * Check the strength of a password
     *
     * @param request password strength check request
     * @return API response with the password strength
     */
    ApiResponse<CheckPasswordStrengthResponse> checkPasswordStrength(CheckPasswordStrengthRequest request);

    /**
     * Export passwords
     *
     * @param format export format (CSV, JSON)
     * @param includePasswords whether to include decrypted passwords
     * @return API response with the exported passwords
     */
    ApiResponse<ExportPasswordsResponse> exportPasswords(String format, boolean includePasswords);

    /**
     * Import passwords
     *
     * @param request password import request
     * @return API response with import results
     */
    ApiResponse<ImportPasswordsResponse> importPasswords(ImportPasswordsRequest request);
}
