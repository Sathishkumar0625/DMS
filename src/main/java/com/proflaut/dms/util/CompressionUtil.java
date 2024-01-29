package com.proflaut.dms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

public class CompressionUtil {
	
	 public static String compressAndReturnB64(String base64Content) throws IOException {
	        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
	        byte[] compressedBytes = compress(decodedBytes);
	        return Base64.getEncoder().encodeToString(compressedBytes);
	    }

	    private static byte[] compress(byte[] content) throws IOException {
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
	            gzipOutputStream.write(content);
	        }
	        return byteArrayOutputStream.toByteArray();
	    }

    public static String decompressB64(String b64Compressed) throws IOException {
        byte[] decompressedBytes = decompress(Base64.getDecoder().decode(b64Compressed));
        return new String(decompressedBytes, StandardCharsets.UTF_8);
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
