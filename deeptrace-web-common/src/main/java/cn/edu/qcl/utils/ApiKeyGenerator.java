package cn.edu.qcl.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * API Key Generator, used to generate secure API Keys and validate their validity.
 * <p>
 * API Key structure: sk_[random-part].[timestamp].[signature]
 * - sk_: prefix indicating this is a Secret Key
 * - random-part: 16 bytes of random data, encoded using URL-safe Base64
 * - timestamp: milliseconds since epoch when the API Key was generated
 * - signature: HmacSHA256 signature of random-part and timestamp
 * </p>
 * <p>
 * API Keys are permanent (no expiry) and can be deleted but not revoked.
 * </p>
 */
@Component
public class ApiKeyGenerator {
    /** HMAC algorithm name used for signature generation */
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    /** Length of random bytes */
    private static final int RANDOM_BYTES_LENGTH = 16;
    /** API Key prefix */
    private static final String API_KEY_PREFIX = "sk_";

    /** Secret key used for HMAC signature */
    private final SecretKeySpec secretKeySpec;
    /** Secure random number generator */
    private final SecureRandom secureRandom;


    /**
     * Creates an ApiKeyGenerator instance.
     * @param secretKey key for generating and validating signatures, cannot be null or empty
     * @throws IllegalArgumentException If secretKey is null or empty
     */
    public ApiKeyGenerator(@Value("${api.key.secret}") String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }

        this.secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates a new API Key.
     * The generated API Key is permanent (no expiry time).
     *
     * @return Generated API Key string
     * @throws ApiKeyGenerationException If failed to generate API Key
     */
    public String generateApiKey() {
        try {
            // Generate random bytes
            byte[] randomBytes = new byte[RANDOM_BYTES_LENGTH];
            secureRandom.nextBytes(randomBytes);
            String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

            // Get current timestamp
            long timestamp = Instant.now().toEpochMilli();
            String timestampPart = String.valueOf(timestamp);

            // Generate signature
            String dataToSign = randomPart + "." + timestampPart;
            String signature = generateSignature(dataToSign);

            // Combine parts with prefix
            return API_KEY_PREFIX + randomPart + "." + timestampPart + "." + signature;
        } catch (Exception e) {
            throw new ApiKeyGenerationException("Failed to generate API key", e);
        }
    }

    /**
     * Validates the format and signature of an API Key.
     * Note: This method only validates the API Key format and signature.
     * The actual validity (whether it exists and is not deleted) should be checked against the database.
     *
     * @param apiKey API Key string to validate
     * @return true if API Key format and signature are valid, false otherwise
     */
    public boolean validateApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }

        // Check prefix
        if (!apiKey.startsWith(API_KEY_PREFIX)) {
            return false;
        }

        // Remove prefix for further validation
        String keyWithoutPrefix = apiKey.substring(API_KEY_PREFIX.length());

        try {
            // Check format
            String[] parts = keyWithoutPrefix.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String randomPart = parts[0];
            String timestampPart = parts[1];
            String signature = parts[2];

            // Check random part and signature are not empty
            if (randomPart.isEmpty() || signature.isEmpty()) {
                return false;
            }

            // Check timestamp format
            try {
                Long.parseLong(timestampPart);
            } catch (NumberFormatException e) {
                return false;
            }

            // Verify signature
            String dataToSign = randomPart + "." + timestampPart;
            String expectedSignature = generateSignature(dataToSign);

            return signature.equals(expectedSignature);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if the given string looks like an API Key (has the correct prefix).
     *
     * @param token Token string to check
     * @return true if the string starts with API Key prefix
     */
    public boolean isApiKey(String token) {
        return token != null && token.startsWith(API_KEY_PREFIX);
    }

    /**
     * Generates a signature using HMAC algorithm.
     *
     * @param data Data to sign
     * @return Generated signature string, encoded using URL-safe Base64
     * @throws NoSuchAlgorithmException If HMAC algorithm is not available
     * @throws InvalidKeyException If secret key is invalid
     */
    private String generateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
    }

    /**
     * Tests API Key generation and validation functionality.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Test API Key generation and validation
        String testSecretKey = "test-secret-key-for-manual-testing";
        ApiKeyGenerator generator = new ApiKeyGenerator(testSecretKey);
        // Generate API Key
        String apiKey = generator.generateApiKey();
        System.out.println("Generated API Key: " + apiKey);

        // Validate API Key
        boolean isValid = generator.validateApiKey(apiKey);
        System.out.println("API Key is valid: " + isValid);

        // Test with invalid API Key
        String invalidApiKey = apiKey + "invalid";
        boolean isInvalid = generator.validateApiKey(invalidApiKey);
        System.out.println("Invalid API Key is valid: " + isInvalid);

        // Test isApiKey method
        System.out.println("Is API Key: " + generator.isApiKey(apiKey));
        System.out.println("Is API Key (JWT token): " + generator.isApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"));
    }

    /**
     * API Key Generation Exception, used to indicate errors during API Key generation.
     */
    public static class ApiKeyGenerationException extends RuntimeException {
        /**
         * Creates an ApiKeyGenerationException with specified message and cause.
         *
         * @param message Exception message
         * @param cause Exception cause
         */
        public ApiKeyGenerationException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Creates an ApiKeyGenerationException with specified message.
         *
         * @param message Exception message
         */
        public ApiKeyGenerationException(String message) {
            super(message);
        }
    }
}