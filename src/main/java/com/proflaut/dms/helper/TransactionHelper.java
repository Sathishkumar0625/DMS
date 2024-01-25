package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfActivitiesEntity;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.entity.ProfDmsMainEntity;
import com.proflaut.dms.entity.ProfExecutionEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfGetExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.service.impl.FolderServiceImpl;

@Component
public class TransactionHelper {
	
	private final Random random = new Random();
	
	@Autowired
	FolderServiceImpl folderServiceImpl;
	
	private String generateUniqueId() {
		return String.format("%04d", this.random.nextInt(999));
	}

	public ProfActivitiesEntity convertReqtoProfActEnti(ProfActivityRequest activityRequest,
			ProfUserInfoEntity entity) {
		ProfActivitiesEntity activitiesEntity = new ProfActivitiesEntity();
		activitiesEntity.setCreatedAt(LocalDateTime.now().toString());
		activitiesEntity.setGroupId(activityRequest.getGroupId());
		activitiesEntity.setKey(activityRequest.getKey());
		activitiesEntity.setProcessId(activityRequest.getProcessId());
		activitiesEntity.setTitle(activityRequest.getTitle());
		activitiesEntity.setStatus("A");
		activitiesEntity.setUserID(activityRequest.getUserID());
		activitiesEntity.setCreatedBy(entity.getUserName());
		return activitiesEntity;
	}

	public ProfDmsMainEntity convertMakerReqToMakerEntity(ProfDmsMainRequest mainRequest) throws CustomException {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setAccountNo(mainRequest.getAccountNo());
		mainEntity.setBranchcode(mainRequest.getBranchcode());
		mainEntity.setBranchName(mainRequest.getBranchName());
		mainEntity.setCustomerId(mainRequest.getCustomerId());
		mainEntity.setIfsc(mainRequest.getIfsc());
		mainEntity.setName(mainRequest.getName());
		mainEntity.setUserId(mainRequest.getUserId());
		mainEntity.setKey(mainRequest.getKey());
		String uniqueId = generateUniqueId();
		mainEntity.setProspectId("DMS_" + uniqueId);
		FolderFO folderFO = new FolderFO();
		folderFO.setProspectId("DMS_" + uniqueId);
		folderServiceImpl.saveFolder(folderFO);
		return mainEntity;
	}

	public ProfDmsMainReterive convertMainEntityToMainReterive(ProfDmsMainEntity dmsMainEntity) {
		ProfDmsMainReterive dmsMainReterive = new ProfDmsMainReterive();
		dmsMainReterive.setAccountNumber(dmsMainEntity.getAccountNo());
		dmsMainReterive.setBranchCode(dmsMainEntity.getBranchcode());
		dmsMainReterive.setBranchName(dmsMainEntity.getBranchName());
		dmsMainReterive.setCustomerId(dmsMainEntity.getCustomerId());
		dmsMainReterive.setIfsc(dmsMainEntity.getIfsc());
		dmsMainReterive.setName(dmsMainEntity.getName());
		dmsMainReterive.setProspectId(dmsMainEntity.getProspectId());
		return dmsMainReterive;
	}

	public ProfDmsMainEntity convertUpdateDmsReqToDmsEntity(ProfUpdateDmsMainRequest dmsMainRequest,
			ProfDmsMainEntity mainEntity) {
		mainEntity.setAccountNo(dmsMainRequest.getAccountNo());
		mainEntity.setBranchcode(dmsMainRequest.getBranchCode());
		mainEntity.setCustomerId(dmsMainRequest.getCustomerId());
		mainEntity.setIfsc(dmsMainRequest.getIfsc());
		mainEntity.setName(dmsMainRequest.getName());
		mainEntity.setBranchName(dmsMainRequest.getBranch());
		return mainEntity;
	}

	public ProfDmsHeader convertjsontoHeaderEntity(String jsonData) {
		ProfDmsHeader dmsHeader = new ProfDmsHeader();
		dmsHeader.setKey("maker");
		dmsHeader.setFields(convertToJsonString(jsonData));
		return dmsHeader;
	}

	private String convertToJsonString(String jsonData) {
		try {
			jsonData = jsonData.replace("\r", "").replace("\n", "");
			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			return "Something Went Wrong";
		}
	}
	public ProfDmsMainEntity convertRequestToProfMain(int userId, String activityName,
			ProfExecutionEntity executionEntity) {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setKey(activityName);
		mainEntity.setUserId(userId);
		mainEntity.setProspectId(executionEntity.getProspectId());
		return mainEntity;
	}

	public ProfExecutionEntity convertRequestToProfHeader(String activityName, ProfUserInfoEntity entity) {
		ProfExecutionEntity executionEntity = new ProfExecutionEntity();
		executionEntity.setActionBy(entity.getUserName());
		executionEntity.setActivityName(activityName);
		executionEntity.setEntryDate(LocalDateTime.now().toString());
		String uniqueId = generateUniqueId();
		executionEntity.setProspectId("DMS_" + uniqueId);
		executionEntity.setStatus("IN PROGRESS");
		return executionEntity;
	}

	public ProfGetExecutionResponse convertExecutionToGetExecution(ProfExecutionEntity profExecutionEntity) {
		ProfGetExecutionResponse executionResponse = new ProfGetExecutionResponse();
		executionResponse.setKey(profExecutionEntity.getActivityName());
		executionResponse.setProspectId(profExecutionEntity.getProspectId());
		return executionResponse;
	}
}
