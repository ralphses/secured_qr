package com.clicks.secured_qr_backend.config.security;

import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Utility class for handling encryption and decryption of API keys using AES algorithm.
 */
@RequiredArgsConstructor
public class ApiKeyUtils {

    private final String secKey;

    /**
     * Generates a valid AES key based on the given key string.
     *
     * @param keyString The input key string.
     * @return A valid AES SecretKey.
     */
    private SecretKey generateValidAESKey(String keyString) {
        byte[] keyBytes = keyString.getBytes();

        // AES supports key lengths of 128, 192, or 256 bits
        int validKeyLength = 16; // 128 bits

        if (keyBytes.length < validKeyLength) {
            // Pad the key with zeroes if it's too short
            byte[] paddedKey = new byte[validKeyLength];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return new SecretKeySpec(paddedKey, "AES");
        } else if (keyBytes.length > validKeyLength) {
            // Truncate the key if it's too long
            byte[] truncatedKey = new byte[validKeyLength];
            System.arraycopy(keyBytes, 0, truncatedKey, 0, validKeyLength);
            return new SecretKeySpec(truncatedKey, "AES");
        } else {
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    /**
     * Encrypts the given input using AES algorithm.
     *
     * @param input The input string to be encrypted.
     * @return The Base64-encoded encrypted string.
     * @throws Exception If encryption fails.
     */
    public String encrypt(String input) throws Exception {
        SecretKey secretKey = generateValidAESKey(secKey);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts the given encrypted input using AES algorithm.
     *
     * @param encryptedInput The Base64-encoded encrypted string.
     * @return The decrypted string.
     * @throws Exception If decryption fails.
     */
    public String decrypt(String encryptedInput) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey secretKey = generateValidAESKey(secKey);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedInput);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }
}
