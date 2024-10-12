package com.zerowhisper.shorturlserver.service;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.Base64;

@Component
public class LongToBase64Encoder {
    public static String encodeLongToBase64(long value) {
        // Convert long to byte array
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);

        // Encode the byte array to Base64
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    public static long decodeBase64ToLong(String base64Encoded) {
        // Decode the Base64 string into a byte array
        byte[] longBytes = Base64.getDecoder().decode(base64Encoded);

        // Convert the byte array back to a long
        ByteBuffer buffer = ByteBuffer.wrap(longBytes);
        return buffer.getLong();
    }
}
