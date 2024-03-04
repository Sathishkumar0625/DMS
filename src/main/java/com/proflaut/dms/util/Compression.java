package com.proflaut.dms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Compression {
	private Compression() {
	}

	private static final Logger logger = LogManager.getLogger(Compression.class);

	public static String compressAndReturnB64(String text) throws IOException {
		byte[] deflateCompressed = deflateCompress(text.getBytes(StandardCharsets.UTF_8));
		logger.info("Original Text Length --> {} ", text.length());
		logger.info("Deflate Compressed Length --> {} ", deflateCompressed.length);

		return Base64.getEncoder().encodeToString(deflateCompressed);
	}

	public static String decompressB64(String compressedBase64) throws IOException {
		byte[] compressedBytes = Base64.getDecoder().decode(compressedBase64);
		byte[] decompressedBytes = inflateDecompress(compressedBytes);

		return new String(decompressedBytes, StandardCharsets.UTF_8);
	}

	private static byte[] deflateCompress(byte[] data) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(data.length);

		try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(byteStream, new Deflater())) {
			deflaterStream.write(data);
		}

		return byteStream.toByteArray();
	}

	private static byte[] inflateDecompress(byte[] data) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(data.length);

		try (InflaterInputStream inflaterStream = new InflaterInputStream(new ByteArrayInputStream(data))) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inflaterStream.read(buffer)) != -1) {
				byteStream.write(buffer, 0, bytesRead);
			}
		}

		return byteStream.toByteArray();
	}
}
