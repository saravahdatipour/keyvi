package keyvi.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordGenerator {

    public static String generateSecurePassword() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[24]; // Generate 24 bytes of random data
        random.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes); // Base64 encode to make it a usable string
    }
}
