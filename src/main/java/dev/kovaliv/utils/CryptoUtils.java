package dev.kovaliv.utils;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class CryptoUtils {

    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @SneakyThrows
    public static String encrypt(String text) {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));
        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @SneakyThrows
    public static String decrypt(String text) {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(getIV()));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(decryptedBytes);
    }

    private static byte[] getIV() {
        return new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f};
    }

    @SneakyThrows
    private static Key getSecretKey() {
        String encryptionKey = System.getenv("ENCRYPTION_KEY");
        if (encryptionKey == null || encryptionKey.isEmpty()) {
            throw new RuntimeException("'ENCRYPTION_KEY' environment is not set");
        }
        return new SecretKeySpec(
                encryptionKey.getBytes(StandardCharsets.UTF_8),
                0,
                encryptionKey.length(),
                "AES"
        );
    }
}
