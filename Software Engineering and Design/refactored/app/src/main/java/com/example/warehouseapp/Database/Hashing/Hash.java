package com.example.warehouseapp.Database.Hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Hash {

    /**
     * Method to generate a random cryptographic salt using secure random.
     * This salt is later combined with a password to make the hash unique per user,
     * preventing rainbow table attacks.
     * @return Base64 encoded string
     */
    public static String generateSalt() {
        // create a byte array to hold the salt (16 bytes = 128 bits)
        byte[] salt = new byte[16];

        // fill the byte array with secure random bytes
        new SecureRandom().nextBytes(salt);

        // Encode the byte array to a string
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes the given password combined with the provided salt using SHA-256
     * This ensures that the same password will produce a unique hash for each unique salt.
     *
     * @param password The user's raw password input
     * @param salt Base64 Salt string
     * @return A base64 string representing the hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            // Get SHA 256 MD instance for hashing
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Include the salt in the digest so it's hashed alongisde the password
            md.update(salt.getBytes());

            // Hash the salted password and retrieve the byte array result
            byte[] hashed = md.digest(password.getBytes());

            // encode the resulting hash as a Base64 string for storage
            return Base64.getEncoder().encodeToString(hashed);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Hashing Algorithm not found", e);
        }
    }



}


