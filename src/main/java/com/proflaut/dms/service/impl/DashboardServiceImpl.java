package com.proflaut.dms.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.DashboardDataEntity;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfDownloadHistoryEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfGroupUserMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.helper.ProfUserUploadDetailsResponse;
import com.proflaut.dms.model.ImageRequest;
import com.proflaut.dms.model.ImageResponse;
import com.proflaut.dms.model.ProfUserGroupDetailsResponse;
import com.proflaut.dms.repository.DashBoardRepository;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfDownloadHistoryRepo;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfGroupUserMappingRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

	private static final Logger logger = LogManager.getLogger(DashboardServiceImpl.class);
	ProfUserInfoRepository infoRepository;
	ProfDocUploadRepository docUploadRepository;
	FileHelper fileHelper;
	ProfUserGroupMappingRepository groupMappingRepository;
	ProfGroupInfoRepository groupInfoRepository;
	ProfGroupUserMappingRepository userMappingRepository;
	ProfUserPropertiesRepository userPropertiesRepository;
	RestTemplate restTemplate;
	ProfDownloadHistoryRepo downloadHistoryRepo;
	FolderRepository folderRepository;
	DashBoardRepository boardRepository;

	@Autowired
	public DashboardServiceImpl(ProfUserInfoRepository infoRepository, ProfDocUploadRepository docUploadRepository,
			FileHelper fileHelper, ProfUserGroupMappingRepository groupMappingRepository,
			ProfGroupInfoRepository groupInfoRepository, ProfGroupUserMappingRepository userMappingRepository,
			ProfUserPropertiesRepository userPropertiesRepository, RestTemplate restTemplate,
			ProfDownloadHistoryRepo downloadHistoryRepo, FolderRepository folderRepository,
			DashBoardRepository boardRepository) {
		this.infoRepository = infoRepository;
		this.docUploadRepository = docUploadRepository;
		this.fileHelper = fileHelper;
		this.groupMappingRepository = groupMappingRepository;
		this.groupInfoRepository = groupInfoRepository;
		this.userMappingRepository = userMappingRepository;
		this.userPropertiesRepository = userPropertiesRepository;
		this.restTemplate = restTemplate;
		this.downloadHistoryRepo = downloadHistoryRepo;
		this.folderRepository = folderRepository;
		this.boardRepository = boardRepository;
	}

	private List<Map<String, String>> userCounts = new ArrayList<>();

	public String formatCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDateTime.format(formatter);
	}

	@Override
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return detailsResponse;
	}

	public String usersCount() {
		long count = 0;
		try {
			count = infoRepository.count();
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return String.valueOf(count);
	}

	public List<Map<String, String>> averageFileUpload(String token) {
		List<Map<String, String>> barGraphList = new ArrayList<>();
		try {
			ProfUserPropertiesEntity propertiesEntity = userPropertiesRepository.findByToken(token);
			List<DashboardDataEntity> dataEntities = boardRepository.findByUserName(propertiesEntity.getUserName());
			for (DashboardDataEntity dataEntity : dataEntities) {
				Map<String, String> entry = new LinkedHashMap<>();
				String date = dataEntity.getDate();
				String avgFileUpload = dataEntity.getAvgFileSize();

				entry.put("date", date);
				entry.put("avgFileUpload", avgFileUpload.replace("kb", ""));

				barGraphList.add(entry);

				if (barGraphList.size() > 10) {
					barGraphList.remove(0);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return barGraphList;
	}

	public List<ImageResponse> getOcrImage(ImageRequest imageRequest) {
		List<ImageResponse> imageResponses = new ArrayList<>();
		try {
			byte[] byteArray = Base64Utils.decodeFromString(imageRequest.getImage());
			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			// Create request entity with image file
			HttpEntity<Resource> requestEntity = new HttpEntity<>(new ByteArrayResource(byteArray), headers);

			// Send POST request to Python server
			ResponseEntity<ImageResponse> responseEntity = restTemplate.exchange("http://127.0.0.1:5000/ocrImage",
					HttpMethod.POST, requestEntity, ImageResponse.class);

			ImageResponse responseBody = responseEntity.getBody();
			if (responseBody != null && responseBody.getSharpenImage() != null) {
				String[] text = responseBody.getSharpenImage().split("\r\n|\r|\n");
				for (String response : text) {
					if (!response.trim().isEmpty()) {
						ImageResponse imageResponse = new ImageResponse();
						imageResponse.setSharpenImage(response.trim());
						imageResponses.add(imageResponse);
					}
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}

		return imageResponses;
	}

	public ProfUserUploadDetailsResponse getUserFullDetails(String token) {
		ProfUserUploadDetailsResponse detailsResponse = new ProfUserUploadDetailsResponse();
		try {
			ProfUserPropertiesEntity entity = userPropertiesRepository.findByToken(token);
			int userId = entity.getUserId();
			if (entity.getUserName() != null) {
				long count = docUploadRepository.countByCreatedBy(entity.getUserName());
				detailsResponse.setUserUploadedCount(String.valueOf(count));
				List<ProfDocEntity> docEntities = docUploadRepository.findByCreatedBy(entity.getUserName());
				long userDocSize = fileHelper.getTotalFileSize(docEntities);
				detailsResponse.setUserFileOccupiedSize(String.valueOf(userDocSize) + "kb");
				List<ProfDownloadHistoryEntity> historyEntities = downloadHistoryRepo.findByUserId(userId);
				detailsResponse.setUserDownloadCount(String.valueOf(historyEntities.size()));
				List<FolderEntity> folderEntities = folderRepository.findByCreatedBy(entity.getUserName());
				detailsResponse.setUserFolderCreated(String.valueOf(folderEntities.size()));
				String average = calculateAverage(count, userDocSize);
				detailsResponse.setAverageFileUploade(average);
				String averageExecutionTime = calculateAverageExecutionTime(docEntities);
				detailsResponse.setAverageUploadSpeed(averageExecutionTime);
				String averageDownloadSpeed = calculateAverageDownloadSpeed(historyEntities.size(), historyEntities);
				detailsResponse.setAverageDownloadSpeed(averageDownloadSpeed);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return detailsResponse;
	}

	public String calculateAverageDownloadSpeed(int size, List<ProfDownloadHistoryEntity> historyEntities) {
		long sum = historyEntities.stream().mapToLong(ProfDownloadHistoryEntity::getDownloadExecutionSpeed).sum();
		return sum / size + "ms";
	}

	public String calculateAverageExecutionTime(List<ProfDocEntity> docEntities) {
		if (docEntities.isEmpty()) {
			return null;
		}
		long sum = docEntities.stream().mapToLong(ProfDocEntity::getUploadExecutionTime).sum();
		return sum / docEntities.size() + "ms";
	}

	public String calculateAverage(long count, long userDocSize) {
		long average = (userDocSize / count);
		return average + "kb";
	}

	@Override
	public List<Map<String, String>> linearGraph(String token) {
		List<Map<String, String>> linearGraphList = new ArrayList<>();
		try {
			ProfUserPropertiesEntity entity = userPropertiesRepository.findByToken(token);
			List<DashboardDataEntity> dataEntities = boardRepository.findByUserName(entity.getUserName());

			for (DashboardDataEntity dataEntity : dataEntities) {
				Map<String, String> entry = new LinkedHashMap<>();
				String date = dataEntity.getDate();
				String avgUploadSpeed = dataEntity.getAvgUploadSpeed();
				String avgDownloadSpeed = dataEntity.getAvgDownloadSpeed();

				entry.put("date", date);
				entry.put("avgUploadSpeed", avgUploadSpeed.replace("ms", ""));
				entry.put("avgDownloadSpeed", avgDownloadSpeed.replace("ms", ""));

				linearGraphList.add(entry);

				if (linearGraphList.size() > 10) {
					linearGraphList.remove(0);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return linearGraphList;
	}

	@Override
	public List<Map<String, String>> totalUploadDownloadGraph(String token) {
		List<Map<String, String>> uploadDownl = new ArrayList<>();
		try {
			ProfUserPropertiesEntity entity = userPropertiesRepository.findByToken(token);

			List<DashboardDataEntity> dataEntities = boardRepository.findByUserName(entity.getUserName());

			for (DashboardDataEntity dataEntity : dataEntities) {
				Map<String, String> entry = new LinkedHashMap<>();
				String date = dataEntity.getDate();
				String totalDownl = dataEntity.getTotalDownloads();
				String totalUplo = dataEntity.getTotalUploads();

				entry.put("date", date);
				entry.put("Total Download", totalDownl);
				entry.put("Total Upload", totalUplo);

				uploadDownl.add(entry);

				if (uploadDownl.size() > 10) {
					uploadDownl.remove(0);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return uploadDownl;
	}

}
