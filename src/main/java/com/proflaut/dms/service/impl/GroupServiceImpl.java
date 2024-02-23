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
import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.GroupHelper;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.ProfAssignUserRequest;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfGroupInfoResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfSignupUserRequest;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfGroupUserMappingRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
@Transactional
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

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfGroupUserMappingRepository groupUserMappingRepository;

	public ProfGroupInfoResponse updateGroup(int id, ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfGroupInfoEntity groupInfo = groupInfoRepository.findById(id);
			if (groupInfo != null) {
				ProfGroupInfoEntity updatedGroupInfo = groupHelper.updateGroupInfoEnt(groupInfoRequest, groupInfo);
				groupInfoRepository.save(updatedGroupInfo);
				groupInfoResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				groupInfoResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public ProfGroupInfoResponse createGroup(ProfGroupInfoRequest groupInfoRequest, String token)
			throws CustomException {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			if (groupHelper.usernameExists(groupInfoRequest.getGroupName())) {
				throw new CustomException("Group Name already exists");
			}
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
			if (entity.getToken() != null) {
				ProfGroupInfoEntity groupInfoEntity = groupHelper.convertGroupInfoReqToGroupInfoEnt(groupInfoRequest,
						entity);
				groupInfoRepository.save(groupInfoEntity);
				groupInfoResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				groupInfoResponse.setStatus(DMSConstant.FAILURE);
			}
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
			List<ProfUserGroupMappingEntity> mappingEntity = groupHelper
					.convertMappingInfoReqToMappingInfoEnt(mappingRequest);
			mappingRepository.saveAll(mappingEntity);
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

	public ProfMetaDataResponse createTableFromFieldDefinitions(CreateTableRequest createTableRequest) {
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			String tableName = groupHelper.createTable(createTableRequest.getFields(), createTableRequest);
			ProfMetaDataEntity dataEntity = groupHelper.convertTableReqToMetaEntity(createTableRequest, tableName);
			entityManager.persist(dataEntity);
			metaDataResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaDataResponse;
	}

	public ProfGroupInfoResponse updateSignupUser(ProfSignupUserRequest userRequest, int userId) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfUserInfoEntity entity = userInfoRepository.findByUserId(userId);
			if (entity != null) {
				ProfUserInfoEntity infoEntity = groupHelper.convertRequestToUser(userRequest, entity);
				userInfoRepository.save(infoEntity);
				groupInfoResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				groupInfoResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public List<ProfOverallGroupInfoResponse> findById(int userId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = new ArrayList<>();
		try {
			ProfUserInfoEntity entity = userInfoRepository.findByUserId(userId);
			if (entity != null) {
				List<ProfGroupInfoEntity> infoEntity = groupInfoRepository.findByUserId(userId);
				for (ProfGroupInfoEntity profGroupInfoEntity : infoEntity) {
					if (!profGroupInfoEntity.getStatus().equalsIgnoreCase("I")) {
						ProfOverallGroupInfoResponse groupInfoResponse = groupHelper
								.convertActiveGroupInfo(profGroupInfoEntity);
						groupInfoResponses.add(groupInfoResponse);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponses;
	}

	public ProfGroupInfoResponse createAssignUser(ProfAssignUserRequest assignUserRequest) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			List<ProfGroupUserMappingEntity> userMappingEntities = groupHelper
					.convertAssignUserReqToProfGroupUser(assignUserRequest);
			groupUserMappingRepository.saveAll(userMappingEntities);
			groupInfoResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public List<ProfOveralUserInfoResponse> getUsersByGroupId(int groupId) {
		List<ProfOveralUserInfoResponse> userInfoResponses = new ArrayList<>();
		try {
			List<ProfGroupInfoEntity> groupInfoEntities = groupInfoRepository.getById(groupId);
			if (!groupInfoEntities.isEmpty()) {
				for (ProfGroupInfoEntity profGroupInfoEntity : groupInfoEntities) {
					List<ProfUserInfoEntity> infoEntities = userInfoRepository
							.getByUserId(profGroupInfoEntity.getUserId());
					ProfOveralUserInfoResponse infoResponse = groupHelper.convertEntityToResponse(infoEntities);
					userInfoResponses.add(infoResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userInfoResponses;
	}

	public List<ProfOverallGroupInfoResponse> getAssignGroupinfo(int userId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = new ArrayList<>();
		try {
			List<ProfUserGroupMappingEntity> groupInfoEntities = mappingRepository.findByUserId(userId);
			if (!groupInfoEntities.isEmpty()) {
				for (ProfUserGroupMappingEntity profGroupInfoEntity : groupInfoEntities) {
					List<ProfGroupInfoEntity> infoEntities = groupInfoRepository
							.getById(Integer.valueOf(profGroupInfoEntity.getGroupId()));
					ProfOverallGroupInfoResponse infoResponse = groupHelper.convertGroupInfoToResponse(infoEntities);
					groupInfoResponses.add(infoResponse);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponses;
	}

	public ProfGroupInfoResponse deleteAssGroup(int groupId, int userId) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfUserGroupMappingEntity groupMappingEntity = mappingRepository
					.findByGroupIdAndUserId(String.valueOf(groupId), userId);
			if (groupMappingEntity != null) {
				mappingRepository.delete(groupMappingEntity);
				groupInfoResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				groupInfoResponse.setErrorMessage(DMSConstant.USERID_NOT_EXIST);
				groupInfoResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public List<ProfOveralUserInfoResponse> getAssignUserinfo(int groupId) {
		List<ProfOveralUserInfoResponse> overalUserInfoResponses = new ArrayList<>();
		try {
			List<ProfGroupUserMappingEntity> entity = groupUserMappingRepository.findByGroupId(groupId);
			if (!entity.isEmpty()) {
				for (ProfGroupUserMappingEntity groupUserMappingEntity : entity) {
					List<ProfUserInfoEntity> infoEntities = userInfoRepository
							.getByUserId(groupUserMappingEntity.getUserId());
					ProfOveralUserInfoResponse overalUserInfoResponse = groupHelper
							.convertGroupUserToResponse(infoEntities);
					overalUserInfoResponses.add(overalUserInfoResponse);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return overalUserInfoResponses;
	}

	public ProfGroupInfoResponse deleteAssUsers(int groupId, int userId) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfGroupUserMappingEntity groupUserMappingEntity = groupUserMappingRepository
					.findByGroupIdAndUserId(String.valueOf(groupId), userId);
			if (groupUserMappingEntity != null) {
				groupUserMappingRepository.delete(groupUserMappingEntity);
				groupInfoResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				groupInfoResponse.setErrorMessage(DMSConstant.USERID_NOT_EXIST);
				groupInfoResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

}
