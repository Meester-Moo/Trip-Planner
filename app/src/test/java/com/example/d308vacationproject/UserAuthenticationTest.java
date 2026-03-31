package com.example.d308vacationproject;

import com.example.d308vacationproject.database.PasswordHasher;

import org.junit.Test;

import static org.junit.Assert.*;


public class UserAuthenticationTest {

    // Tests that the hashing method produces the same hash twice with the same password
    @Test
    public void testSamePasswordProducesSameHash() {
        String hash1 = PasswordHasher.hashPassword("myPassword123");
        String hash2 = PasswordHasher.hashPassword("myPassword123");
        assertEquals(hash1, hash2);
    }

    // Tests that the hashing method is indeed producing different hashes with different passwords
    @Test
    public void testDifferentPasswordsProduceDifferentHashes() {
        String hash1 = PasswordHasher.hashPassword("password1");
        String hash2 = PasswordHasher.hashPassword("password2");
        assertNotEquals(hash1, hash2);
    }

    // Tests that the hash is not in plaintext
    @Test
    public void testHashIsNotPlaintext() {
        String password = "myPassword123";
        String hash = PasswordHasher.hashPassword(password);
        assertNotEquals(password, hash);
    }

    // Tests that the hash produced is not Null
    @Test
    public void testHashIsNotNull() {
        String hash = PasswordHasher.hashPassword("testPassword");
        assertNotNull(hash);
    }

    // Tests that the hash produced is not an empty string
    @Test
    public void testHashIsNotEmpty() {
        String hash = PasswordHasher.hashPassword("testPassword");
        assertFalse(hash.isEmpty());
    }

    // Tests that the hashing method is case-sensitive
    @Test
    public void testCaseSensitivePasswords() {
        String hashLower = PasswordHasher.hashPassword("password");
        String hashUpper = PasswordHasher.hashPassword("Password");
        assertNotEquals(hashLower, hashUpper);
    }

    // Tests that the password "null" does not literally equal null
    @Test
    public void testLiteralNullPassword() {
        String password = "null";
        String hash = PasswordHasher.hashPassword(password);
        assertNotEquals(null, hash);
    }

    // Tests that special characters in the password do not produce null
    @Test
    public void testSpecialCharacters() {
        String password = "!@#$%^&*()_+1234567890-=<>?:{},./;'[]`~'";
        String hash = PasswordHasher.hashPassword(password);
        assertEquals(PasswordHasher.hashPassword(password), hash);
        assertNotEquals(null, hash);
    }
}
