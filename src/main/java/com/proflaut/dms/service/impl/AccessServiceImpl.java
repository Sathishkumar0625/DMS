package com.proflaut.dms.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.customException.CustomExcep;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.AccessHelper;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.ProfUserLogoutResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.util.TokenGenerator;

@Service
public class AccessServiceImpl {

	@Autowired
	ProfUserInfoRepository profUserInfoRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	AccessHelper accessHelper;

	@Autowired
	TokenGenerator tokenGenerator;

	private static final Logger logger = LogManager.getLogger(AccessServiceImpl.class);

	public Map<String, String> validateToken(String token) {
		Map<String, String> resp = new HashMap<>();
		try {
			ProfUserPropertiesEntity userProp = profUserPropertiesRepository.findByToken(token);
			if (userProp != null) {
				userProp.setLastUsed(LocalDateTime.now().toString());
				profUserPropertiesRepository.save(userProp);
				resp.put("status", "success");
				resp.put("userId", userProp.getUserId().toString());
			} else {

				resp.put("status", "failure");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;

	}

	public UserRegResponse saveUser(UserInfo userInfo) throws CustomException {
		UserRegResponse userRegResponse = new UserRegResponse();
		try {
			if (accessHelper.usernameExists(userInfo.getUserName())) {
				throw new CustomExcep("Username already exists");
			}
			userInfo.setCreatedDate(Timestamp.from(Instant.now()));
			logger.info("USER INFO --->{}", userInfo);
			userRegResponse.setEmail(userInfo.getEmail());
			userRegResponse.setUserName(userInfo.getUserName());
			ProfUserInfoEntity ent = accessHelper.convertUserInfotoProfUser(userInfo);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.save(ent);
			userRegResponse.setStatus(DMSConstant.SUCCESS);
			userRegResponse.setErrorMessage(DMSConstant.NA);
			userRegResponse.setUserId(profUserInfoEntity.getUserId());

		} catch (Exception e) {

			e.printStackTrace();
			throw new CustomException(e.getMessage());

		}
		return userRegResponse;
	}

	public LoginResponse getUser(UserInfo userInfo) {
		LoginResponse loginResponse = new LoginResponse();
		try {

			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserName(userInfo.getUserName());
			if (profUserInfoEntity != null) {
				validation(userInfo, profUserInfoEntity, loginResponse);
			} else {
				loginResponse.setStatus(DMSConstant.FAILURE);
				loginResponse.setErrorMessage("Username is not valid");
				loginResponse.setUserId(0);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return loginResponse;
	}

	private void validation(UserInfo userInfo, ProfUserInfoEntity profUserInfoEntity, LoginResponse loginResponse)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		String token = "";
		boolean isValidate = accessHelper.validatePassword(profUserInfoEntity, userInfo);
		if (isValidate) {
			Map<String, String> tokenResp = tokenGenerator.generateToken(userInfo.getUserName());
			token = tokenResp.get("token");
			ProfUserPropertiesEntity ent = accessHelper.convertUserInfotoProfUserProp(profUserInfoEntity, tokenResp);
			userValidation(userInfo, profUserInfoEntity, loginResponse, token, ent);
		} else {
			loginResponse.setStatus(DMSConstant.FAILURE);
			loginResponse.setErrorMessage("Password is invalid");
			loginResponse.setUserId(profUserInfoEntity.getUserId());
		}

	}

	private void userValidation(UserInfo userInfo, ProfUserInfoEntity profUserInfoEntity, LoginResponse loginResponse,
			String token, ProfUserPropertiesEntity ent) {
		if (userInfo.getUseForceLogin().equals("Y")) {
			profUserPropertiesRepository.deleteByUserId(profUserInfoEntity.getUserId());
			ProfUserPropertiesEntity profUserPropertiesEntity = profUserPropertiesRepository.save(ent);
			if (!StringUtils.isEmpty(token)) {
				loginResponse.setToken(token);
				loginResponse.setUserId(profUserPropertiesEntity.getId());
				loginResponse.setLastLogin(profUserPropertiesEntity.getLastLogin());
				loginResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				loginResponse.setStatus(DMSConstant.FAILURE);
				loginResponse.setErrorMessage("Error in updating user properties");
				loginResponse.setUserId(profUserInfoEntity.getUserId());
			}

		} else {
			ProfUserPropertiesEntity profUserPropertiesEntity = profUserPropertiesRepository
					.findByUserId(profUserInfoEntity.getUserId());
			if (profUserPropertiesEntity == null) {
				profUserPropertiesEntity = profUserPropertiesRepository.save(ent);
				if (!StringUtils.isEmpty(token)) {
					loginResponse.setToken(token);
					loginResponse.setUserId(profUserPropertiesEntity.getId());
					loginResponse.setLastLogin(profUserPropertiesEntity.getLastLogin());
					loginResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					loginResponse.setStatus(DMSConstant.FAILURE);
					loginResponse.setErrorMessage("Error in updating user properties");
					loginResponse.setUserId(profUserInfoEntity.getUserId());
				}
			} else {
				loginResponse.setToken(profUserPropertiesEntity.getToken());
				loginResponse.setUserId(profUserPropertiesEntity.getId());
				loginResponse.setLastLogin(profUserPropertiesEntity.getLastLogin());
				loginResponse.setStatus(DMSConstant.SUCCESS);
			}
		}

	}

	public ProfUserLogoutResponse deleteUser(int userId) {
		ProfUserLogoutResponse logoutResponse = new ProfUserLogoutResponse();
		try {
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByUserId(userId);
			if (entity != null) {
				profUserPropertiesRepository.delete(entity);
				logoutResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				logoutResponse.setStatus(DMSConstant.FAILURE);
				logoutResponse.setStatus(DMSConstant.USERID_NOT_EXIST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logoutResponse;
	}
}
