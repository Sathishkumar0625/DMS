package com.proflaut.dms.helper;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;

@Component
public class AccessHelper {
	
	@Autowired
	ProfUserInfoRepository userInfoRepository;
	
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


}
