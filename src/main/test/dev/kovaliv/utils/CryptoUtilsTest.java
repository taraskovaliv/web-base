package dev.kovaliv.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
class CryptoUtilsTest {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Test
    void encryptAndDecrypt() {
        String encryptionKeyEnvName = "ENCRYPTION_KEY";
        String encryptionKey = "1234567890123456";
        environmentVariables.set(encryptionKeyEnvName, encryptionKey);
        Assertions.assertEquals(encryptionKey, System.getenv(encryptionKeyEnvName));
        String text = "Hello, World!";
        String encrypted = CryptoUtils.encrypt(text);
        String decrypted = CryptoUtils.decrypt(encrypted);
        Assertions.assertEquals(text, decrypted);
    }
}