package com.sureprompt.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class EncryptionUtilsTest {

    private EncryptionUtils encryptionUtils;

    @BeforeEach
    void setUp() {
        encryptionUtils = new EncryptionUtils();
        ReflectionTestUtils.setField(
                encryptionUtils,
                "secretKey",
                "0123456789abcdef0123456789abcdef"
        );
    }

    @Test
    void encryptAndDecryptRoundTrip() {
        String plainText = "sk-test-1234567890";

        String encrypted = encryptionUtils.encrypt(plainText);
        String decrypted = encryptionUtils.decrypt(encrypted);

        assertThat(encrypted).isNotBlank();
        assertThat(decrypted).isEqualTo(plainText);
    }

    @Test
    void encryptingSamePlainTextTwiceProducesDifferentCipherText() {
        String plainText = "repeatable-input";

        String encrypted1 = encryptionUtils.encrypt(plainText);
        String encrypted2 = encryptionUtils.encrypt(plainText);

        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }
}
