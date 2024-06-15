package keyvi.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AccountMasker {

    public static String generateMaskedEmail(String originalEmail, String domain, String key) {
        String randomPart = generateRandomPart(originalEmail);
        return randomPart + "-" + key + domain;
    }

    private static String generateRandomPart(String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 12);  // Take the first 12 characters of the SHA-256 hex string
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }

    public static String generateHashedUsername(String originalEmail) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(originalEmail.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);  // URL-safe base64
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }
}
