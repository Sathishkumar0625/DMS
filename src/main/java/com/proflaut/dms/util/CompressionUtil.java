package com.proflaut.dms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

public class CompressionUtil {
	
    public static String compressAndReturnB64(String text) throws IOException {
        byte[] compressedBytes = compress(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(compressedBytes);
    }

    public static String decompressB64(String b64Compressed) throws IOException {
        byte[] decompressedBytes = decompress(Base64.getDecoder().decode(b64Compressed));
        return new String(decompressedBytes, StandardCharsets.UTF_8);
    }

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (XZCompressorOutputStream los = new XZCompressorOutputStream(baos)) {
            los.write(data);
        }
        return baos.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (XZCompressorInputStream lis = new XZCompressorInputStream(bais)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = lis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }
}
