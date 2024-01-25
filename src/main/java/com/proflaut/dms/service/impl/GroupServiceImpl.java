package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.GroupHelper;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfGroupInfoResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;

@Service
public class GroupServiceImpl {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	ProfGroupInfoRepository groupInfoRepository;

	@Autowired
	GroupHelper groupHelper;

	@Autowired
	ProfUserGroupMappingRepository mappingRepository;

	@Autowired
	ProfUserInfoRepository userInfoRepository;

	public String updateStatus(Integer id, ProfGroupInfoRequest groupInfoRequest) {
		try {
			Optional<ProfGroupInfoEntity> groupInfo = groupInfoRepository.findById(id);
			groupInfo.get().setStatus("I");
			groupInfoRepository.save(groupInfo.get());
			return DMSConstant.SUCCESS;
//			groupInfoRepository.findById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return DMSConstant.FAILURE;
		}

	}

	public ProfGroupInfoResponse createGroup(ProfGroupInfoRequest groupInfoRequest) throws CustomException {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			if (groupHelper.usernameExists(groupInfoRequest.getGroupName())) {
				throw new CustomException("Group Name already exists");
			}

			ProfGroupInfoEntity groupInfoEntity = groupHelper.convertGroupInfoReqToGroupInfoEnt(groupInfoRequest);
			groupInfoRepository.save(groupInfoEntity);
			groupInfoResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}
		return groupInfoResponse;

	}

	public List<ProfOverallGroupInfoResponse> find() {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = new ArrayList<>();
		try {
			List<ProfGroupInfoEntity> groupInfoEntities = groupInfoRepository.findAll();

			for (ProfGroupInfoEntity profGroupInfoEntity : groupInfoEntities) {
				if (!profGroupInfoEntity.getStatus().equalsIgnoreCase("I")) {
					ProfOverallGroupInfoResponse response = groupHelper.convertToResponse(profGroupInfoEntity);
					groupInfoResponses.add(response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponses;
	}

	public ProfGroupInfoResponse createGroup(ProfUserGroupMappingRequest mappingRequest) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfUserGroupMappingEntity mappingEntity = groupHelper
					.convertMappingInfoReqToMappingInfoEnt(mappingRequest);
			mappingRepository.save(mappingEntity);
			groupInfoResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			groupInfoResponse.setStatus(DMSConstant.FAILURE);
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public List<ProfOveralUserInfoResponse> findUsers() {
		List<ProfOveralUserInfoResponse> infoResponses = new ArrayList<>();
		try {
			List<ProfUserInfoEntity> groupInfoEntities = userInfoRepository.findAll();

			for (ProfUserInfoEntity profUserInfoEntity : groupInfoEntities) {
				if (!profUserInfoEntity.getStatus().equalsIgnoreCase("I")) {
					ProfOveralUserInfoResponse response = groupHelper.convertToOveralUserResponse(profUserInfoEntity);
					infoResponses.add(response);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return infoResponses;
	}

	@Transactional
	public ProfMetaDataResponse createTableFromFieldDefinitions(CreateTableRequest createTableRequest) {
		ProfMetaDataResponse metaDataResponse=new ProfMetaDataResponse();
	    try {
	    		String tableName=groupHelper.createTable(createTableRequest.getFields(),createTableRequest);
	    		ProfMetaDataEntity dataEntity=groupHelper.convertTableReqToMetaEntity(createTableRequest,tableName);
		        entityManager.persist(dataEntity);
		        metaDataResponse.setStatus(DMSConstant.SUCCESS);
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return metaDataResponse;
	}

}
