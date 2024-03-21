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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proflaut.dms.configuration.TwilioConfig;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.customexception.CustomExcep;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.AccessHelper;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.ProfForgotpassResponse;
import com.proflaut.dms.model.ProfUserLogoutResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;
import com.proflaut.dms.util.TokenGenerator;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Service
public class AccessServiceImpl {

	private CacheManager cacheManager;

	private Cache otpCache;

	ProfUserInfoRepository profUserInfoRepository;
	ProfUserPropertiesRepository profUserPropertiesRepository;
	AccessHelper accessHelper;
	TokenGenerator tokenGenerator;
	TwilioConfig twilioConfig;
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	public AccessServiceImpl(ProfUserInfoRepository profUserInfoRepository,
			ProfUserPropertiesRepository profUserPropertiesRepository, AccessHelper accessHelper,
			TokenGenerator tokenGenerator, RedisTemplate<String, String> redisTemplate, TwilioConfig twilioConfig,
			CacheManager cacheManager) {
		this.profUserInfoRepository = profUserInfoRepository;
		this.profUserPropertiesRepository = profUserPropertiesRepository;
		this.accessHelper = accessHelper;
		this.tokenGenerator = tokenGenerator;
		this.redisTemplate = redisTemplate;
		this.twilioConfig = twilioConfig;
		this.cacheManager = cacheManager;
		otpCache = cacheManager.getCache("otpCache");
	}

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
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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

			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			throw new CustomException(e.getMessage());

		}
		return userRegResponse;
	}

	public LoginResponse getUser(UserInfo userInfo) {
		LoginResponse loginResponse = new LoginResponse();
		try {

			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserName(userInfo.getUserName());
			if (!profUserInfoEntity.getStatus().equalsIgnoreCase("I")) {
				validation(userInfo, profUserInfoEntity, loginResponse);
			} else {
				loginResponse.setStatus(DMSConstant.FAILURE);
				loginResponse.setErrorMessage("Username is not valid Or User is Inactive");
				loginResponse.setUserId(0);
			}

		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return loginResponse;
	}

	private void validation(UserInfo userInfo, ProfUserInfoEntity profUserInfoEntity, LoginResponse loginResponse)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		String token = "";
		boolean isValidate = accessHelper.validatePassword(profUserInfoEntity, userInfo);
		if (isValidate) {
			Map<String, String> tokenResp = TokenGenerator.generateToken(userInfo.getUserName());
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
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findById(userId);
			if (entity != null) {
				profUserPropertiesRepository.delete(entity);
				logoutResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				logoutResponse.setStatus(DMSConstant.FAILURE);
				logoutResponse.setErrorMessage(DMSConstant.USERID_NOT_EXIST);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return logoutResponse;
	}

	public ProfForgotpassResponse forgotPassword(String mailId) {
		ProfForgotpassResponse forgotpassResponse = new ProfForgotpassResponse();
		try {
			ProfUserInfoEntity infoEntity = profUserInfoRepository.findByEmail(mailId);
			if (infoEntity != null) {
				String otp = accessHelper.generateOTP();
				long validityDurationMinutes = 4;
				accessHelper.sendOTP(mailId, otp, validityDurationMinutes);

				// Storing OTP in cache
				otpCache.put(new Element(mailId, otp));

				// Set response status and message
				forgotpassResponse.setStatus(DMSConstant.SUCCESS);
				forgotpassResponse.setMessage("OTP SENT SUCCESSFULLY");
			}
		} catch (Exception e) {
			// Handle exception
			forgotpassResponse.setStatus(DMSConstant.FAILURE);
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return forgotpassResponse;
	}

	public ProfForgotpassResponse verifyOtp(Map<String, String> data) {
		ProfForgotpassResponse forgotpassResponse = new ProfForgotpassResponse();
		try {
			String email = data.get("email");
			String otp = data.get("otp");

			// Retrieving OTP from cache
			Element element = otpCache.get(email);
			if (element != null) {
				String storedOTP = (String) element.getObjectValue();
				if (otp.equals(storedOTP)) {
					// Clear OTP from storage after verification
					otpCache.remove(email);
					forgotpassResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					forgotpassResponse.setStatus(DMSConstant.FAILURE);
					forgotpassResponse.setMessage(DMSConstant.INVALID_OTP);
				}
			} else {
				forgotpassResponse.setStatus(DMSConstant.FAILURE);
				forgotpassResponse.setMessage(DMSConstant.INVALID_OTP + " or expired");
			}
		} catch (Exception e) {
			forgotpassResponse.setStatus(DMSConstant.FAILURE);
			forgotpassResponse.setMessage("Error verifying OTP");
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return forgotpassResponse;
	}

	public ProfForgotpassResponse savePass(Map<String, String> data) {
		ProfForgotpassResponse forgotpassResponse = new ProfForgotpassResponse();
		try {
			PasswordEncDecrypt encDecrypt = new PasswordEncDecrypt();
			String email = data.get("email");
			String newPassword = data.get("password");
			ProfUserInfoEntity infoEntity = profUserInfoRepository.findByEmail(email);
			infoEntity.setPassword(encDecrypt.encrypt(newPassword));
			profUserInfoRepository.save(infoEntity);
			forgotpassResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return forgotpassResponse;
	}

	public ProfForgotpassResponse modifyStatus(int userId) {
		ProfForgotpassResponse forgotpassResponse = new ProfForgotpassResponse();
		try {
			ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(userId);
			if (!infoEntity.getUserName().isEmpty()) {
				infoEntity.setStatus("I");
				profUserInfoRepository.save(infoEntity);
				forgotpassResponse.setStatus(DMSConstant.SUCCESS);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return forgotpassResponse;
	}
}
