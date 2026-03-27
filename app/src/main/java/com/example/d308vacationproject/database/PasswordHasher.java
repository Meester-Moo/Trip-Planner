package com.example.d308vacationproject.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// A utility class for hashing passwords using SHA-256
// SHA-256 converts a string into a fixed-length hash that cannot be reversed
public class PasswordHasher {

    // Takes a plaintext password string and returns its SHA-256 hash as a hexadecimal string.
    // Example: "mypassword" → "5e884898da28047151d0e56f8dc..."
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
