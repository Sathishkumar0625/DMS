package com.proflaut.dms.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.helper.ProfUserUploadDetailsResponse;
import com.proflaut.dms.model.ProfUserGroupDetailsResponse;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfGroupUserMappingRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class DashboardServiceImpl {

	@Autowired
	ProfUserInfoRepository infoRepository;

	@Autowired
	ProfDocUploadRepository docUploadRepository;

	@Autowired
	FileHelper fileHelper;

	@Autowired
	ProfUserGroupMappingRepository groupMappingRepository;

	@Autowired
	ProfGroupInfoRepository groupInfoRepository;

	@Autowired
	ProfGroupUserMappingRepository userMappingRepository;

	@Autowired
	ProfUserPropertiesRepository userPropertiesRepository;

	private List<Map<String, String>> userCounts = new ArrayList<>();

	public String formatCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDateTime.format(formatter);
	}

	public List<Map<String, String>> getUserCounts() {
		return userCounts;
	}

	public ProfUserUploadDetailsResponse getUploadedDetails(String token) {
		ProfUserUploadDetailsResponse detailsResponse = new ProfUserUploadDetailsResponse();
		try {
			ProfUserPropertiesEntity entity = userPropertiesRepository.findByToken(token);
			int userId = entity.getUserId();
			if (entity.getUserName() != null) {
				long count = docUploadRepository.countByCreatedBy(entity.getUserName());
				detailsResponse.setUserUploadedCount(String.valueOf(count));
				List<ProfDocEntity> docEntities = docUploadRepository.findByCreatedBy(entity.getUserName());
				long userDocSize = fileHelper.getTotalFileSize(docEntities);
				detailsResponse.setUserFileOccupiedSize(String.valueOf(userDocSize));
				long noOfGroupAssignd = userMappingRepository.countByUserId(userId);
				detailsResponse.setNoOfGroupAssigned(String.valueOf(noOfGroupAssignd));
				List<ProfGroupInfoEntity> groupInfoEntities = groupInfoRepository.findByUserId(userId);
				List<Integer> listOfGroupId = groupInfoEntities.stream().map(ProfGroupInfoEntity::getId)
						.collect(Collectors.toList());
				List<Integer> userIds = new ArrayList<>();

				for (Integer groupId : listOfGroupId) {
					List<Integer> userIdsForGroup = userMappingRepository.findUserIdsByGroupId(groupId);
					userIds.addAll(userIdsForGroup);
				}
				List<String> userNames = infoRepository.findUserNamesByUserIds(userIds);
				long totalFileSize = 0;

				for (String userName : userNames) {
					List<ProfDocEntity> entities = docUploadRepository.findByCreatedBy(userName);
					long userFileSize = fileHelper.getTotalFileSize(entities);
					totalFileSize += userFileSize;
				}

				long userGroupFileOccupiedSize = totalFileSize - userDocSize;
				detailsResponse.setUserGroupFileOccupiedSize(String.valueOf(userGroupFileOccupiedSize));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailsResponse;
	}

	public List<ProfUserGroupDetailsResponse> getUserDetails(String token) {
		List<ProfUserGroupDetailsResponse> detailsResponse = new ArrayList<>();
		try {
			ProfUserPropertiesEntity entity = userPropertiesRepository.findByToken(token);
			int userId = entity.getUserId();
			List<ProfGroupUserMappingEntity> groupInfoEntity = userMappingRepository.findByUserId(userId);
			for (ProfGroupUserMappingEntity profGroupInfoEntity : groupInfoEntity) {
				ProfUserGroupDetailsResponse response = new ProfUserGroupDetailsResponse();
				ProfGroupInfoEntity ent = groupInfoRepository.findById(profGroupInfoEntity.getGroupId());
				response.setGroupName(ent.getGroupName());
				long userGroupCount = groupInfoRepository.countByUserId(userId);
				response.setGroupCount(String.valueOf(userGroupCount));
				List<Integer> userIds = userMappingRepository.findUserIdsByGroupId(profGroupInfoEntity.getGroupId());
				List<String> groupMembers = new ArrayList<>();
				if (!userIds.isEmpty()) {
					for (Integer userIdss : userIds) {
						ProfUserInfoEntity userInfoEntity = infoRepository.findByUserId(userIdss);
						if (userInfoEntity != null) {
							groupMembers.add(userInfoEntity.getUserName());
						}
					}
					response.setGroupMembers(groupMembers);
				}
				if (!userIds.isEmpty()) {
					List<ProfUserInfoEntity> infoEntities = infoRepository.findByUserIdIn(userIds);
					List<String> userNames = infoEntities.stream().map(ProfUserInfoEntity::getUserName)
							.collect(Collectors.toList());
					List<ProfDocEntity> entities = docUploadRepository.findByCreatedByIn(userNames);
					long totaluserFileSize = fileHelper.getTotalFileSize(entities);
					response.setGroupUploadedFileSize(String.valueOf(totaluserFileSize));
				}
				detailsResponse.add(response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailsResponse;
	}

	public String usersCount() {
		long count = 0;
		try {
			count = infoRepository.count();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.valueOf(count);
	}

	@Scheduled(fixedRate = 2 * 24 * 60 * 60 * 1000)
//	@Scheduled(fixedRate = 10 * 1000)
	public void updateUserCount() {
		String count = usersCount();
		String date = formatCurrentDate();
		Map<String, String> countMap = new HashMap<>();
		countMap.put("date", date);
		countMap.put("count", count);
		userCounts.add(countMap);
		if (userCounts.size() > 10) {
			userCounts.remove(0);
		}
	}

}
