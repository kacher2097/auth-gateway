package com.authenhub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for password operations
 */
@Slf4j
@Component
public class PasswordUtils {

    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_-+=<>?/[]{}|";

    private final SecureRandom random = new SecureRandom();

    /**
     * Generate a random password
     *
     * @param length the length of the password
     * @param includeLowercase include lowercase letters
     * @param includeUppercase include uppercase letters
     * @param includeNumbers include numbers
     * @param includeSpecial include special characters
     * @return the generated password
     */
    public String generateRandomPassword(int length, boolean includeLowercase, boolean includeUppercase,
                                         boolean includeNumbers, boolean includeSpecial) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be positive");
        }

        if (!(includeLowercase || includeUppercase || includeNumbers || includeSpecial)) {
            throw new IllegalArgumentException("At least one character type must be included");
        }

        StringBuilder validChars = new StringBuilder();
        List<Character> mandatoryChars = new ArrayList<>();

        if (includeLowercase) {
            validChars.append(LOWERCASE_CHARS);
            mandatoryChars.add(LOWERCASE_CHARS.charAt(random.nextInt(LOWERCASE_CHARS.length())));
        }

        if (includeUppercase) {
            validChars.append(UPPERCASE_CHARS);
            mandatoryChars.add(UPPERCASE_CHARS.charAt(random.nextInt(UPPERCASE_CHARS.length())));
        }

        if (includeNumbers) {
            validChars.append(NUMBERS);
            mandatoryChars.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }

        if (includeSpecial) {
            validChars.append(SPECIAL_CHARS);
            mandatoryChars.add(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));
        }

        // Ensure the password length is at least as long as the number of mandatory characters
        if (length < mandatoryChars.size()) {
            length = mandatoryChars.size();
        }

        char[] password = new char[length];

        // First, add all mandatory characters
        for (int i = 0; i < mandatoryChars.size(); i++) {
            password[i] = mandatoryChars.get(i);
        }

        // Then fill the rest with random characters
        for (int i = mandatoryChars.size(); i < length; i++) {
            password[i] = validChars.charAt(random.nextInt(validChars.length()));
        }

        // Shuffle the password to avoid predictable patterns
        for (int i = 0; i < password.length; i++) {
            int j = random.nextInt(password.length);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }

        return new String(password);
    }

    /**
     * Check the strength of a password
     *
     * @param password the password to check
     * @return the strength score (0-100)
     */
    public PasswordStrength checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return new PasswordStrength(0, "Password is empty");
        }

        int score = 0;
        List<String> feedback = new ArrayList<>();

        // Length check
        int length = password.length();
        if (length < 8) {
            feedback.add("Password is too short (minimum 8 characters)");
            score += 10;
        } else if (length < 12) {
            feedback.add("Password could be longer for better security");
            score += 20;
        } else if (length < 16) {
            score += 30;
        } else {
            score += 40;
        }

        // Character variety checks
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();

        if (!hasLower) {
            feedback.add("Add lowercase letters");
        } else {
            score += 10;
        }

        if (!hasUpper) {
            feedback.add("Add uppercase letters");
        } else {
            score += 10;
        }

        if (!hasDigit) {
            feedback.add("Add numbers");
        } else {
            score += 10;
        }

        if (!hasSpecial) {
            feedback.add("Add special characters");
        } else {
            score += 10;
        }

        // Check for common patterns
        if (Pattern.compile("(\\w)\\1{2,}").matcher(password).find()) {
            feedback.add("Avoid repeated characters");
            score -= 10;
        }

        if (Pattern.compile("(?i)(password|123456|qwerty|admin)").matcher(password).find()) {
            feedback.add("Avoid common passwords");
            score -= 20;
        }

        // Sequential characters check
        if (Pattern.compile("(?i)abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz").matcher(password).find() ||
            Pattern.compile("012|123|234|345|456|567|678|789").matcher(password).find()) {
            feedback.add("Avoid sequential characters");
            score -= 10;
        }

        // Ensure score is within bounds
        score = Math.max(0, Math.min(100, score));

        String strengthLabel;
        if (score < 30) {
            strengthLabel = "Very Weak";
        } else if (score < 50) {
            strengthLabel = "Weak";
        } else if (score < 70) {
            strengthLabel = "Moderate";
        } else if (score < 90) {
            strengthLabel = "Strong";
        } else {
            strengthLabel = "Very Strong";
        }

        return new PasswordStrength(score, strengthLabel, feedback);
    }

    /**
     * Class to represent password strength
     */
    public static class PasswordStrength {
        private final int score;
        private final String strength;
        private final List<String> feedback;

        public PasswordStrength(int score, String strength) {
            this(score, strength, new ArrayList<>());
        }

        public PasswordStrength(int score, String strength, List<String> feedback) {
            this.score = score;
            this.strength = strength;
            this.feedback = feedback;
        }

        public int getScore() {
            return score;
        }

        public String getStrength() {
            return strength;
        }

        public List<String> getFeedback() {
            return feedback;
        }
    }
}
