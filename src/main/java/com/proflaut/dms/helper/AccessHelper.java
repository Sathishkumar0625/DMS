package com.proflaut.dms.helper;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.service.impl.AccessServiceImpl;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AccessHelper {

	ProfUserInfoRepository userInfoRepository;

	private JavaMailSender mailSender;

	private static final Logger logger = LogManager.getLogger(AccessHelper.class);

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public ProfUserInfoEntity convertUserInfotoProfUser(UserInfo userInfo) throws InvalidKeyException,
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		PasswordEncDecrypt td = new PasswordEncDecrypt();
		String encrypted = td.encrypt(userInfo.getPassword());
		logger.info("USER PASSWORD ---> {}", userInfo);
		ProfUserInfoEntity ent = new ProfUserInfoEntity();
		ent.setStatus("A");
		ent.setAdminAccesss(userInfo.getAdminAccess());
		ent.setWebAccess(userInfo.getWebAccess());
		ent.setEmail(userInfo.getEmail());
		ent.setPassword(encrypted);
		ent.setUserName(userInfo.getUserName());
		ent.setCreatedDate(formatCurrentDateTime());
		ent.setMobileNo(userInfo.getMobileNo());
		ent.setLocation(userInfo.getLocation());
		ent.setLdap(userInfo.getLdap());
		return ent;
	}

	public boolean validatePassword(ProfUserInfoEntity profUserInfoEntity, UserInfo userInfo)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		PasswordEncDecrypt td = new PasswordEncDecrypt();
		String decryptPassword = td.decrypt(profUserInfoEntity.getPassword());
		boolean isValidate;
		if (decryptPassword.equals(userInfo.getPassword())) {
			isValidate = true;
		} else {
			isValidate = false;
		}
		return isValidate;
	}

	public boolean usernameExists(String userName) {
		ProfUserInfoEntity entity = userInfoRepository.findByUserName(userName);
		return entity != null;
	}

	public ProfUserPropertiesEntity convertUserInfotoProfUserProp(ProfUserInfoEntity profUserInfoEntity,
			Map<String, String> tokenResp) {
		ProfUserPropertiesEntity ent = new ProfUserPropertiesEntity();
		ent.setToken(tokenResp.get("token"));
		ent.setSecKey(tokenResp.get("seckey"));
		ent.setUserId(profUserInfoEntity.getUserId());
		String localdateandtime = LocalDateTime.now().toString();
		ent.setLastLogin(localdateandtime);
		ent.setUserName(profUserInfoEntity.getUserName());
		return ent;
	}

	public String generateOTP() {
	    SecureRandom secureRandom = new SecureRandom();
	    int otp = 100000 + secureRandom.nextInt(900000);
	    return String.valueOf(otp);
	}

	public void sendOTP(String email, String otp, long validityDurationMinutes) {
		long validityDurationSeconds = TimeUnit.MINUTES.toSeconds(validityDurationMinutes);
		String validityDurationText = formatValidityDuration(validityDurationSeconds);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Password Reset OTP");
		message.setText(
				"Your OTP for password reset is: " + otp + ". This OTP is valid for " + validityDurationText + ".");

		mailSender.send(message);
	}

	private String formatValidityDuration(long validityDurationSeconds) {
		long minutes = TimeUnit.SECONDS.toMinutes(validityDurationSeconds);
		long remainingSeconds = validityDurationSeconds - TimeUnit.MINUTES.toSeconds(minutes);

		StringBuilder builder = new StringBuilder();
		if (minutes > 0) {
			builder.append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
			if (remainingSeconds > 0) {
				builder.append(" and ");
			}
		}
		if (remainingSeconds > 0) {
			builder.append(remainingSeconds).append(" second").append(remainingSeconds > 1 ? "s" : "");
		}

		return builder.toString();
	}

}
