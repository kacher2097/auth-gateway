package com.authenhub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utility class for encrypting and decrypting sensitive data like passwords
 */
@Slf4j
@Component
public class PasswordEncryptionUtil {

    @Value("${password.encryption.secret:defaultSecretKey}")
    private String secretKey;

    @Value("${password.encryption.salt:defaultSaltValue}")
    private String salt;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;

    /**
     * Encrypts a password
     *
     * @param plainText the plain text password to encrypt
     * @return the encrypted password with IV prepended, Base64 encoded
     */
    public String encrypt(String plainText) {
        try {
            // Generate a random IV
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // Generate the secret key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Encrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to the encrypted data and encode with Base64
            byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedIVAndText);
        } catch (Exception e) {
            log.error("Error encrypting password: {}", e.getMessage(), e);
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    /**
     * Decrypts an encrypted password
     *
     * @param encryptedText the encrypted password with IV prepended, Base64 encoded
     * @return the decrypted plain text password
     */
    public String decrypt(String encryptedText) {
        try {
            // Decode from Base64
            byte[] encryptedIVAndText = Base64.getDecoder().decode(encryptedText);

            // Extract IV
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[encryptedIVAndText.length - 16];
            System.arraycopy(encryptedIVAndText, 0, iv, 0, iv.length);
            System.arraycopy(encryptedIVAndText, 16, encrypted, 0, encrypted.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // Generate the secret key
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Decrypt
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting password: {}", e.getMessage(), e);
            throw new RuntimeException("Error decrypting password", e);
        }
    }
}
