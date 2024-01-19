package com.proflaut.dms.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfAccountRequestEntity;
import com.proflaut.dms.entity.ProfActivitiesEntity;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.entity.ProfDmsMainEntity;
import com.proflaut.dms.entity.ProfExecutionEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.UserHelper;
import com.proflaut.dms.model.AccountDetailsRequest;
import com.proflaut.dms.model.AccountDetailsResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityResponse;
import com.proflaut.dms.model.ProfActivityReterive;
import com.proflaut.dms.model.ProfDmsHeaderReterive;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfExecutionResponse;
import com.proflaut.dms.model.ProfGetExecutionFinalResponse;
import com.proflaut.dms.model.ProfGetExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.ProfUpdateDmsMainResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.repository.ProfAccountRegisterRepository;
import com.proflaut.dms.repository.ProfActivityRepository;
import com.proflaut.dms.repository.ProfDmsHeaderRepository;
import com.proflaut.dms.repository.ProfDmsMainRepository;
import com.proflaut.dms.repository.ProfExecutionRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.util.TokenGenerator;

@Service
public class UserRegisterServiceImpl {
	@Autowired
	ProfUserInfoRepository profUserInfoRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfAccountRegisterRepository profAccountRegisterRepository;

	@Autowired
	UserHelper helper;

	@Autowired
	TokenGenerator tokenGen;

	@Autowired
	ProfActivityRepository activityRepository;

	@Autowired
	ProfDmsMainRepository dmsMainRepository;

	@Autowired
	ProfDmsHeaderRepository headerRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	FolderServiceImpl folderServiceImpl;

	@Autowired
	ProfExecutionRepository executionRepository;

	@Autowired
	private final JdbcTemplate jdbcTemplate;

	public UserRegisterServiceImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	private static final Logger logger = LogManager.getLogger(UserRegisterServiceImpl.class);

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

			userInfo.setCreatedDate(Timestamp.from(Instant.now()));
			logger.info("USER INFO --->{}", userInfo);
			userRegResponse.setEmail(userInfo.getEmail());
			userRegResponse.setUserName(userInfo.getUserName());
			ProfUserInfoEntity ent = helper.convertUserInfotoProfUser(userInfo);
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

	public LoginResponse getUser(UserInfo userInfo) throws Exception {
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
			throws Exception {
		String token = "";
		boolean isValidate = helper.validatePassword(profUserInfoEntity, userInfo);
		if (isValidate) {
			Map<String, String> tokenResp = tokenGen.generateToken(userInfo.getUserName());
			token = tokenResp.get("token");
			ProfUserPropertiesEntity ent = helper.convertUserInfotoProfUserProp(profUserInfoEntity, tokenResp);
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

	public AccountDetailsResponse addCustomer(AccountDetailsRequest accountDetailsRequest) throws CustomException {
		AccountDetailsResponse accountDetailsResponse = new AccountDetailsResponse();

		try {
			ProfAccountRequestEntity entList = helper.convertCustomerAcctoProfUser(accountDetailsRequest);

			profAccountRegisterRepository.save(entList);

			accountDetailsResponse.setCustomerId(accountDetailsRequest.getCustomerId());
			accountDetailsResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}
		return accountDetailsResponse;
	}

	public ProfActivityResponse saveActivity(ProfActivityRequest activityRequest) {
		ProfActivityResponse activityResponse = new ProfActivityResponse();
		try {
			ProfUserInfoEntity entity = profUserInfoRepository.findByUserId(activityRequest.getUserID());
			if (entity != null) {
				ProfActivitiesEntity activitiesEntity = helper.convertReqtoProfActEnti(activityRequest, entity);
				activityRepository.save(activitiesEntity);
				activityResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				activityResponse.setStatus(DMSConstant.SUCCESS);
				activityResponse.setErrorMessage("User Id Not Found");
			}

		} catch (Exception e) {
			e.printStackTrace();
			activityResponse.setStatus(DMSConstant.FAILURE);
			activityResponse.setErrorMessage("Error occurred while saving activity: " + e.getMessage());
		}
		return activityResponse;
	}

	public List<ProfActivityReterive> retrieveActivitiesByUserId(int userId) {
		List<ProfActivitiesEntity> entities = activityRepository.findByUserID(userId);
		if (!entities.isEmpty()) {
			return entities.stream().map(this::convertEntityToReterive).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public ProfActivityReterive convertEntityToReterive(ProfActivitiesEntity entity) {
		ProfActivityReterive profActivityReterive = new ProfActivityReterive();
		profActivityReterive.setKey(entity.getKey());
		profActivityReterive.setTitle(entity.getTitle());
		profActivityReterive.setStatus(entity.getStatus());
		profActivityReterive.setUserID(entity.getUserID());
		profActivityReterive.setGroupId(entity.getGroupId());
		profActivityReterive.setProcessId(entity.getProcessId());
		profActivityReterive.setCreatedBy(entity.getCreatedBy());
		profActivityReterive.setCreatedAt(entity.getCreatedAt());

		return profActivityReterive;
	}

	public ProfActivityResponse saveMaker(ProfDmsMainRequest mainRequest) {
		ProfActivityResponse activityResponse = new ProfActivityResponse();
		try {
			ProfDmsMainEntity makerEntity = helper.convertMakerReqToMakerEntity(mainRequest);
			dmsMainRepository.save(makerEntity);
			activityResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return activityResponse;
	}

	public List<ProfDmsMainReterive> retrieveByMainUserId(String key, int userId) {
		List<ProfDmsMainReterive> dmsMainReterives = new ArrayList<>();
		try {
			List<ProfDmsMainEntity> dmsMainEntities = dmsMainRepository.findByUserIdAndKey(userId, key);

			for (ProfDmsMainEntity dmsMainEntity : dmsMainEntities) {
				ProfDmsMainReterive dmsMainReterive = helper.convertMainEntityToMainReterive(dmsMainEntity);
				dmsMainReterives.add(dmsMainReterive);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmsMainReterives;
	}

	public ProfUpdateDmsMainResponse updateDmsMain(ProfUpdateDmsMainRequest dmsMainRequest) {
		ProfUpdateDmsMainResponse dmsMainResponse = new ProfUpdateDmsMainResponse();
		try {
			ProfDmsMainEntity dmsMainEntity = dmsMainRepository.findById(dmsMainRequest.getProspectId());
			if (dmsMainEntity != null) {
				ProfDmsMainEntity mainEntity = helper.convertUpdateDmsReqToDmsEntity(dmsMainRequest, dmsMainEntity);
				dmsMainRepository.save(mainEntity);
				dmsMainResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				dmsMainResponse.setStatus(DMSConstant.FAILURE);
				dmsMainResponse.setErrorMessage(DMSConstant.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmsMainResponse;
	}

	public ProfUpdateDmsMainResponse saveJsonData(String jsonData) {
		ProfUpdateDmsMainResponse mainResponse = new ProfUpdateDmsMainResponse();
		try {
			ProfDmsHeader dmsHeader = helper.convertjsontoHeaderEntity(jsonData);
			headerRepository.save(dmsHeader);
			mainResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			mainResponse.setStatus(DMSConstant.FAILURE);

		}

		return mainResponse;
	}

//	public List<ProfDmsHeader> retrieveHeadersByKey(String key) {
//		return headerRepository.findByKey(key);
//	}
	public List<Map<String, Object>> retrieveHeadersByKey(String key) {
		List<ProfDmsHeader> headers = headerRepository.findByKey(key);
		List<Map<String, Object>> headerList = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();

		headers.forEach(header -> {
			try {
				String fields = header.getFields();
				if (fields != null) {
					@SuppressWarnings("unchecked")
					Map<String, Object> fieldsMap = objectMapper.readValue(fields, Map.class);
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> innerHeaders = (List<Map<String, Object>>) fieldsMap.get("headers");
					headerList.addAll(innerHeaders);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		return headerList;
	}

	@Transactional(rollbackFor = { SQLException.class, Exception.class })
	public ProfExecutionResponse saveData(int userId, String activityName) {
		ProfExecutionResponse profExecutionResponse = new ProfExecutionResponse();

		try {
			ProfUserInfoEntity entity = profUserInfoRepository.findByUserId(userId);
			// Do something with the EntityManager such as persist(), merge() or remove()
			ProfExecutionEntity executionEntity = helper.convertRequestToProfHeader(activityName, entity);
			entityManager.persist(executionEntity);
			ProfDmsMainEntity mainEntity = helper.convertRequestToProfMain(userId, activityName, executionEntity);
			entityManager.persist(mainEntity);
			FolderFO folderFO = new FolderFO();
			folderFO.setProspectId(executionEntity.getProspectId());
			folderServiceImpl.saveFolder(folderFO);
			profExecutionResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			profExecutionResponse.setStatus(DMSConstant.FAILURE);
			e.printStackTrace();
		}
		return profExecutionResponse;
	}

	public List<ProfGetExecutionResponse> filterByMaker(String key) {
		List<ProfGetExecutionResponse> executionResponse = new ArrayList<>();
		try {
			List<ProfExecutionEntity> executionEntity = executionRepository.findByActivityName(key);
			for (ProfExecutionEntity profExecutionEntity : executionEntity) {
				ProfGetExecutionResponse executionResponses = helper
						.convertExecutionToGetExecution(profExecutionEntity);
				executionResponse.add(executionResponses);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return executionResponse;
	}

//	public List<ProfGetExecutionFinalResponse> findByProspectId(List<ProfGetExecutionResponse> executionResponse) {
//		List<ProfGetExecutionFinalResponse> finalexecutionResponse = new ArrayList<>();
//		try {
//			for (ProfGetExecutionResponse response : executionResponse) {
//				List<ProfDmsMainEntity> dmsMainEntities = dmsMainRepository.findByProspectId(response.getProspectId());
//				ProfGetExecutionFinalResponse executionFinalResponse = helper
//						.convertMainEntityToFinalResponse(dmsMainEntities);
//				finalexecutionResponse.add(executionFinalResponse);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return finalexecutionResponse;
//	}

//	public List<ProfGetExecutionFinalResponse> convertToResponseList(List<Object[]> resultList) {
//		List<ProfGetExecutionFinalResponse> executionFinalResponses = new ArrayList<>();
//
//		for (Object[] result : resultList) {
//			// Check the length of the result array before accessing its elements
//
//			executionFinalResponses.add(new ProfGetExecutionFinalResponse((String) result[0], (String) result[1],
//					(String) result[2], (String) result[3], (String) result[4], (String) result[5], (String) result[6],
//					(String) result[7], (String) result[8], (String) result[9]));
//		}
//
//		return executionFinalResponses;
//	}

}
