package oopt.assignment.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// This new class handles all password security.
public class PasswordUtil {

    /**
     * Hashes a plain-text password using SHA-256.
     * @param plainPassword The password to hash.
     * @return A hexadecimal string representation of the hash.
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    plainPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 algorithm not found. This should not happen.");
            // In a real app, you might want to throw a RuntimeException
            return null;
        }
    }

    /**
     * Checks if a plain-text password matches a stored hash.
     * @param plainPassword The password entered by the user.
     * @param hashedPassword The hash stored in the file.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        String newHash = hashPassword(plainPassword);
        return newHash != null && newHash.equals(hashedPassword);
    }

    /**
     * Helper method to convert a byte array to a hex string.
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}