package com.proflaut.dms.service.impl;

import java.io.IOException;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfActivitiesEntity;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.entity.ProfDmsMainEntity;
import com.proflaut.dms.entity.ProfExecutionEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.helper.TransactionHelper;
import com.proflaut.dms.model.InvoiceRequest;
import com.proflaut.dms.model.InvoiceResponse;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityResponse;
import com.proflaut.dms.model.ProfActivityReterive;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfExecutionResponse;
import com.proflaut.dms.model.ProfGetExecutionFinalResponse;
import com.proflaut.dms.model.ProfGetExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.ProfUpdateDmsMainResponse;
import com.proflaut.dms.repository.ProfActivityRepository;
import com.proflaut.dms.repository.ProfDmsHeaderRepository;
import com.proflaut.dms.repository.ProfDmsMainRepository;
import com.proflaut.dms.repository.ProfExecutionRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;

@Service
public class TransactionServiceImpl {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	ProfUserInfoRepository profUserInfoRepository;

	ProfActivityRepository activityRepository;

	ProfDmsMainRepository dmsMainRepository;

	FolderServiceImpl folderServiceImpl;

	ProfExecutionRepository executionRepository;

	private EntityManager entityManager;

	ProfDmsHeaderRepository headerRepository;

	TransactionHelper transactionHelper;
	
	
	@Autowired
	public TransactionServiceImpl(ProfUserInfoRepository profUserInfoRepository,
			ProfActivityRepository activityRepository, ProfDmsMainRepository dmsMainRepository,
			FolderServiceImpl folderServiceImpl, ProfExecutionRepository executionRepository,
			ProfDmsHeaderRepository headerRepository, TransactionHelper transactionHelper) {
		this.profUserInfoRepository = profUserInfoRepository;
		this.activityRepository = activityRepository;
		this.dmsMainRepository = dmsMainRepository;
		this.folderServiceImpl = folderServiceImpl;
		this.executionRepository = executionRepository;
		this.headerRepository = headerRepository;
		this.transactionHelper = transactionHelper;
	}

	public ProfActivityResponse saveActivity(ProfActivityRequest activityRequest) {
		ProfActivityResponse activityResponse = new ProfActivityResponse();
		try {
			ProfUserInfoEntity entity = profUserInfoRepository.findByUserId(activityRequest.getUserID());
			if (entity != null) {
				ProfActivitiesEntity activitiesEntity = transactionHelper.convertReqtoProfActEnti(activityRequest,
						entity);
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
			ProfDmsMainEntity makerEntity = transactionHelper.convertMakerReqToMakerEntity(mainRequest);
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
				ProfDmsMainReterive dmsMainReterive = transactionHelper.convertMainEntityToMainReterive(dmsMainEntity);
				dmsMainReterives.add(dmsMainReterive);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dmsMainReterives;
	}

	public ProfUpdateDmsMainResponse updateDmsMain(ProfUpdateDmsMainRequest dmsMainRequest, String prospectId) {
		ProfUpdateDmsMainResponse dmsMainResponse = new ProfUpdateDmsMainResponse();
		try {
			ProfDmsMainEntity dmsMainEntity = dmsMainRepository.findByProspectId(prospectId);
			if (dmsMainEntity != null) {
				ProfDmsMainEntity mainEntity = transactionHelper.convertUpdateDmsReqToDmsEntity(dmsMainRequest,
						dmsMainEntity);
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
			ProfDmsHeader dmsHeader = transactionHelper.convertjsontoHeaderEntity(jsonData);
			headerRepository.save(dmsHeader);
			mainResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
			mainResponse.setStatus(DMSConstant.FAILURE);

		}

		return mainResponse;
	}

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
			ProfExecutionEntity executionEntity = transactionHelper.convertRequestToProfHeader(activityName, entity);
			entityManager.persist(executionEntity);
			ProfDmsMainEntity mainEntity = transactionHelper.convertRequestToProfMain(userId, activityName,
					executionEntity);
			entityManager.persist(mainEntity);
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
				ProfGetExecutionResponse executionResponses = transactionHelper
						.convertExecutionToGetExecution(profExecutionEntity);
				executionResponse.add(executionResponses);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return executionResponse;
	}

	public InvoiceResponse invoice(InvoiceRequest invoiceRequest) {
		InvoiceResponse invoiceResponse = new InvoiceResponse();
		try {
			invoiceResponse = transactionHelper.invoicegenerator(invoiceRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invoiceResponse;
	}

	public Map<String, Object> getExecutionResults(String key, int userId) {
		try {
			String sqlQuery = "SELECT e.prospect_id, e.activity_name, d.* " + "FROM PROF_EXCECUTION e "
					+ "JOIN PROF_DMS_MAIN d ON e.prospect_id = d.prospect_id "
					+ "WHERE e.activity_name = ? AND d.user_id= ?";

			List<ProfGetExecutionFinalResponse> executionFinalResponses = jdbcTemplate.query(sqlQuery,
					new Object[] { key, userId }, new BeanPropertyRowMapper<>(ProfGetExecutionFinalResponse.class));

			List<Map<String, Object>> headers = retrieveHeadersByKey(key);

			Map<String, Object> result = new HashMap<>();
			result.put("headers", headers);
			result.put(key, executionFinalResponses);

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}
}
