package tech.touchgrass.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;

public class PasswordHasher {

    public static class Password {
        private final String hash;
        private final String salt;

        public Password(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash() {
            return hash;
        }

        public String getSalt() {
            return salt;
        }
    }

    public static Password generatePassword(String username, String plaintextPassword) {
        try {
            // Generate a 16-byte salt using username
            byte[] usernameBytes = username.getBytes();
            SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] randomBytes = new byte[16 - Math.min(16, usernameBytes.length)];
            random.nextBytes(randomBytes);

            byte[] saltBytes = new byte[16];
            System.arraycopy(usernameBytes, 0, saltBytes, 0, Math.min(usernameBytes.length, 16));
            System.arraycopy(randomBytes, 0, saltBytes, Math.min(usernameBytes.length, 16), randomBytes.length);

            String salt = Base64.getEncoder().encodeToString(saltBytes);

            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltBytes);
            byte[] hashedPasswordBytes = md.digest(plaintextPassword.getBytes());

            String hash = Base64.getEncoder().encodeToString(hashedPasswordBytes);

            return new Password(hash, salt);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating password hash", e);
        }
    }

    public static boolean verifyPassword(String plaintextPassword, String salt, String storedHash) {
        try {
            // Decode the provided salt
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // Hash the provided password with the provided salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltBytes);
            byte[] hashedPasswordBytes = md.digest(plaintextPassword.getBytes());

            String computedHash = Base64.getEncoder().encodeToString(hashedPasswordBytes);

            // Compare the computed hash with the stored hash
            return computedHash.equals(storedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error verifying password", e);
        }
    }
}
