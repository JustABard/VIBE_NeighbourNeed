package com.example.shopassist.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 100000;
    private static final int KEY_LENGTH = 256;

    private PasswordUtils() {
    }

    public static String generateSalt() throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    public static String hashPassword(String password, String saltBase64) throws Exception {
        byte[] salt = Base64.decode(saltBase64, Base64.NO_WRAP);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.encodeToString(hash, Base64.NO_WRAP);
    }

    public static boolean verifyPassword(String enteredPassword, String storedHash, String storedSalt) throws Exception {
        String newHash = hashPassword(enteredPassword, storedSalt);
        return MessageDigest.isEqual(
                storedHash.getBytes(StandardCharsets.UTF_8),
                newHash.getBytes(StandardCharsets.UTF_8)
        );
    }
}

