package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfAccessUserMappingEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.helper.AccessRightsHelper;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightResponse;
import com.proflaut.dms.model.ProfAccessRightsUpdateRequest;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;

@Service
public class AccessRightsServiceImpl {
	private static final Logger logger = LogManager.getLogger(AccessRightsServiceImpl.class);

	ProfMetaDataRepository dataRepository;
	AccessRightsHelper helper;
	ProfAccessRightRepository accessRightRepository;
	FileManagementServiceImpl serviceImpl;
	ProfAccessUserMappingRepository accessUserMappingRepository;
	ProfAccessGroupMappingRepository accessGroupMappingRepository;
	ProfUserInfoRepository infoRepository;
	GroupServiceImpl groupServiceImpl;
	ProfGroupInfoRepository groupInfoRepository;

	@Autowired
	public AccessRightsServiceImpl(ProfMetaDataRepository dataRepository, AccessRightsHelper helper,
			ProfAccessRightRepository accessRightRepository, FileManagementServiceImpl serviceImpl,
			ProfAccessUserMappingRepository accessUserMappingRepository,
			ProfAccessGroupMappingRepository accessGroupMappingRepository, ProfUserInfoRepository infoRepository,
			GroupServiceImpl groupServiceImpl, ProfGroupInfoRepository groupInfoRepository) {
		this.dataRepository = dataRepository;
		this.helper = helper;
		this.accessRightRepository = accessRightRepository;
		this.serviceImpl = serviceImpl;
		this.accessUserMappingRepository = accessUserMappingRepository;
		this.accessGroupMappingRepository = accessGroupMappingRepository;
		this.infoRepository = infoRepository;
		this.groupServiceImpl = groupServiceImpl;
		this.groupInfoRepository = groupInfoRepository;
	}

	public ProfAccessRightResponse create(ProfAccessRightRequest accessRightRequest) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRights = helper.convertRequestToAccesEntity(accessRightRequest);
			accessRightRepository.save(accessRights);
			accessRightResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage(e.getMessage());
		}
		return accessRightResponse;
	}

	public List<ProfOverallAccessRightsResponse> findAccess() {
		List<ProfOverallAccessRightsResponse> accessRightsResponses = new ArrayList<>();
		try {
			List<ProfAccessRightsEntity> accessRightsEntity = accessRightRepository.findAll();
			for (ProfAccessRightsEntity profAccessRightsEntity : accessRightsEntity) {
				if (!profAccessRightsEntity.getStatus().equalsIgnoreCase("I")) {
					ProfMetaDataEntity dataEntity = dataRepository
							.findById(Integer.parseInt(profAccessRightsEntity.getMetaId()));
					if (dataEntity != null) {
						ProfOverallAccessRightsResponse response = helper
								.convertToOverallResponse(profAccessRightsEntity, dataEntity);
						accessRightsResponses.add(response);
					}
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return accessRightsResponses;
	}

	public ProfOverallAccessRightsResponse findAccessById(String ids) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		try {
			int id = Integer.parseInt(ids);
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(id);
			if (accessRightsEntity != null) {
				ProfMetaDataEntity dataEntity = dataRepository
						.findById(Integer.parseInt(accessRightsEntity.getMetaId()));
				if (dataEntity != null) {
					accessRightsResponse = helper.convertAccessEntityToResponse(accessRightsEntity, dataEntity);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return accessRightsResponse;
	}

	public ProfAccessRightResponse updateAccessRights(ProfAccessRightsUpdateRequest accessRightsUpdateRequest, int id) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(id);
			if (accessRightsEntity != null) {
				ProfAccessRightsEntity updatedEntity = helper.convertRequestToUpdateAccess(accessRightsEntity,
						accessRightsUpdateRequest);
				accessRightRepository.save(updatedEntity);
				accessRightResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				accessRightResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage("An error occurred while updating access rights");
		}
		return accessRightResponse;
	}

	public ProfAccessRightResponse deleteUserAccess(int userId, int accessId) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(accessId);
			if (accessRightsEntity != null) {
				ProfAccessUserMappingEntity accessUserMappingEntity = accessUserMappingRepository
						.findByAccessRightsEntityIdAndUserId(accessId, String.valueOf(userId));
				if (accessUserMappingEntity != null) {
					accessUserMappingRepository.delete(accessUserMappingEntity);
					accessRightResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					accessRightResponse.setStatus(DMSConstant.FAILURE);
					accessRightResponse.setErrorMessage(
							"Access user mapping entity not found for userId " + userId + " and accessId " + accessId);
				}
			} else {
				accessRightResponse.setStatus(DMSConstant.FAILURE);
				accessRightResponse.setErrorMessage("Access rights entity not found for accessId " + accessId);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage("An error occurred while deleting user access");
		}
		return accessRightResponse;
	}

	public ProfAccessRightResponse deleteGroupAccess(int groupId, int accessId) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(accessId);
			if (accessRightsEntity != null) {
				ProfAccessGroupMappingEntity accessGroupMappingEntity = accessGroupMappingRepository
						.findByAccessRightsEntityIdAndGroupId(accessId, String.valueOf(groupId));
				if (accessGroupMappingEntity != null) {
					accessGroupMappingRepository.delete(accessGroupMappingEntity);
					accessRightResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					accessRightResponse.setStatus(DMSConstant.FAILURE);
					accessRightResponse.setErrorMessage("Access group mapping entity not found for groupId " + groupId
							+ " and accessId " + accessId);
				}
			} else {
				accessRightResponse.setStatus(DMSConstant.FAILURE);
				accessRightResponse.setErrorMessage("Access rights entity not found for accessId " + accessId);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage("An error occurred while deleting user access");
		}
		return accessRightResponse;
	}

	public List<ProfOveralUserInfoResponse> findAllNotAccessUsers(int accessId) {
		List<ProfOveralUserInfoResponse> overalUserInfoResponses = new ArrayList<>();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(accessId);
			List<ProfAccessUserMappingEntity> accessUserMappingEntities = accessUserMappingRepository
					.findByAccessRightsEntityId(accessRightsEntity.getId());

			if (!accessUserMappingEntities.isEmpty()) {
				List<String> userIds = accessUserMappingEntities.stream().map(ProfAccessUserMappingEntity::getUserId)
						.collect(Collectors.toList());

				if (!userIds.isEmpty()) {
					List<Integer> userIdsAsInt = userIds.stream().map(Integer::parseInt).collect(Collectors.toList());

					List<ProfUserInfoEntity> infoEntities = infoRepository.findbyUserIdNotIn(userIdsAsInt);
					for (ProfUserInfoEntity infoEntity : infoEntities) {
						ProfOveralUserInfoResponse infoResponse = helper.convertUserInfoToRequest(infoEntity);
						overalUserInfoResponses.add(infoResponse);
					}
				}
			} else {
				overalUserInfoResponses = groupServiceImpl.findUsers();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return overalUserInfoResponses;
	}

	public List<ProfOverallGroupInfoResponse> getAllNotAccessGroups(int accessId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = new ArrayList<>();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(accessId);
			List<ProfAccessGroupMappingEntity> accessGroupMappingEntities = accessGroupMappingRepository
					.findByAccessRightsEntityId(accessRightsEntity.getId());

			if (!accessGroupMappingEntities.isEmpty()) {
				List<String> groupIds = accessGroupMappingEntities.stream()
						.map(ProfAccessGroupMappingEntity::getGroupId).collect(Collectors.toList());

				if (!groupIds.isEmpty()) {
					List<Integer> groupId = groupIds.stream().map(Integer::parseInt).collect(Collectors.toList());

					List<ProfGroupInfoEntity> infoEntities = groupInfoRepository.findbyIdNotIn(groupId);
					for (ProfGroupInfoEntity infoEntity : infoEntities) {
						ProfOverallGroupInfoResponse groupInfoResponse = helper.convertGroupInfoToRequest(infoEntity);
						groupInfoResponses.add(groupInfoResponse);
					}
				}
			} else {
				groupInfoResponses = groupServiceImpl.find();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return groupInfoResponses;
	}

	public ProfAccessRightResponse modifyStatus(int id) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(id);
			if (!StringUtils.isEmpty(accessRightsEntity.getId())) {
				accessRightsEntity.setStatus("I");
				accessRightRepository.save(accessRightsEntity);
				accessRightResponse.setStatus(DMSConstant.SUCCESS);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return accessRightResponse;
	}
}
