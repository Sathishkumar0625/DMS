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

import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

@Component
public class TokenGenerator {

	static Cipher cipher;
	
	
	
	public Map<String, String> generateToken(String userName) {
		String encryptedText = "";
		Map<String, String> resp = new HashMap<>();
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128); // block size is 128bits
			SecretKey secretKey = keyGenerator.generateKey();
			String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
			
			cipher = Cipher.getInstance("AES"); 

			encryptedText = encrypt(userName, secretKey);
			System.out.println("Encrypted Text After Encryption: " + encryptedText);
			resp.put("token", encryptedText);
			resp.put("seckey", encodedKey);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static String encrypt(String plainText, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
		byte[] plainTextByte = plainText.getBytes();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedByte = cipher.doFinal(plainTextByte);
		Base64.Encoder encoder = Base64.getEncoder();
		return  encoder.encodeToString(encryptedByte);
	}

	public static String decrypt(String encryptedText, SecretKey secretKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encryptedTextByte = decoder.decode(encryptedText);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
		return new String(decryptedByte);
	}

}
