package com.proflaut.dms.util;

import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;

@Component
public class TokenGenerator {
	private TokenGenerator() {}
	
	private static final Logger logger = LogManager.getLogger(TokenGenerator.class);
	static Cipher cipher;

	public  Map<String, String> generateToken(String userName) {
		String encryptedText = "";
		Map<String, String> resp = new HashMap<>();
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128); // block size is 128bits
			SecretKey secretKey = keyGenerator.generateKey();
			String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

			cipher = Cipher.getInstance("AES/GCM/NoPadding");

			encryptedText = encrypt(userName, secretKey);
			logger.info("Encrypted Text After Encryption --> {}",encryptedText);
			resp.put("token", encryptedText);
			resp.put("seckey", encodedKey);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return resp;
	}

	public static String encrypt(String plainText, SecretKey secretKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(encryptedByte);
	}

	public static String decrypt(String encryptedText, SecretKey secretKey)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		return new String(decryptedByte);
	}

}
