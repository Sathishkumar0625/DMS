package com.proflaut.dms.helper;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfAccessGroupMappingEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfAccessUserMappingEntity;
import com.proflaut.dms.entity.ProfCheckInAndOutEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.Files;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.model.ProfFolderRetrieveResponse;
import com.proflaut.dms.repository.BookmarkRepository;
import com.proflaut.dms.repository.FileBookmarkRepository;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;
import com.proflaut.dms.repository.ProfCheckInAndOutRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.service.impl.MountPointServiceImpl;

@Component
public class FolderHelper {

	FolderRepository folderRepository;
	@Value("${create.folderlocation}")
	private String folderLocation;
	FolderRepository folderRepo;
	ProfUserPropertiesRepository profUserPropertiesRepository;
	ProfMetaDataRepository dataRepository;
	ProfUserGroupMappingRepository groupMappingRepository;
	ProfAccessGroupMappingRepository accessGroupMappingRepository;
	ProfAccessUserMappingRepository accessUserMappingRepository;
	ProfAccessRightRepository accessRightRepository;
	MountPointServiceImpl mountPointServiceImpl;
	BookmarkRepository bookmarkRepository;
	FileBookmarkRepository fileBookmarkRepository;
	ProfCheckInAndOutRepository checkInAndOutRepository;

	@Autowired
	public FolderHelper(FolderRepository folderRepository, FolderRepository folderRepo,
			ProfUserPropertiesRepository profUserPropertiesRepository, ProfMetaDataRepository dataRepository,
			ProfUserGroupMappingRepository groupMappingRepository,
			ProfAccessGroupMappingRepository accessGroupMappingRepository,
			ProfAccessUserMappingRepository accessUserMappingRepository,
			ProfAccessRightRepository accessRightRepository, MountPointServiceImpl mountPointServiceImpl,
			FileBookmarkRepository fileBookmarkRepository, BookmarkRepository bookmarkRepository,
			ProfCheckInAndOutRepository checkInAndOutRepository) {
		this.folderRepository = folderRepository;
		this.folderRepo = folderRepo;
		this.profUserPropertiesRepository = profUserPropertiesRepository;
		this.dataRepository = dataRepository;
		this.groupMappingRepository = groupMappingRepository;
		this.accessGroupMappingRepository = accessGroupMappingRepository;
		this.accessUserMappingRepository = accessUserMappingRepository;
		this.accessRightRepository = accessRightRepository;
		this.mountPointServiceImpl = mountPointServiceImpl;
		this.bookmarkRepository = bookmarkRepository;
		this.fileBookmarkRepository = fileBookmarkRepository;
		this.checkInAndOutRepository = checkInAndOutRepository;
	}

	private static final Logger logger = LogManager.getLogger(FolderHelper.class);

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public FolderEntity convertFOtoBO(FolderFO folderFO, ProfUserPropertiesEntity propertiesEntity) {

		FolderEntity ent = new FolderEntity();
		ent.setFolderName(folderFO.getFolderName());
		ent.setIsParent(folderLocation);
		ent.setMetaId(folderFO.getMetaDataId());
		ent.setCreatedAt(formatCurrentDateTime());
		ent.setCreatedBy(propertiesEntity.getUserName());
		ent.setParentFolderID(Integer.parseInt(folderFO.getParentFolderID()));
		ent.setCheckIn("NO");
		ent.setCheckOut("YES");
		ent.setStatus("A");
		return ent;
	}

	public Folders convertFolderEntityToFolder(FolderEntity folderEntity, String userName, int userId) {
		ProfFolderBookMarkEntity bookMarkEntity = bookmarkRepository.findByFolderId(folderEntity.getId());
		ProfCheckInAndOutEntity andOutEntity = checkInAndOutRepository
				.findByFolderIdAndFolderNameAndUserId(folderEntity.getId(), folderEntity.getFolderName(), userId);
		Folders folders = new Folders();
		folders.setFolderID(folderEntity.getId());
		folders.setFolderName(folderEntity.getFolderName());
		folders.setIsParent(folderEntity.getIsParent());
		folders.setMetaId(folderEntity.getMetaId());
		folders.setFolderPath(folderEntity.getFolderPath());
		folders.setCreatedAt(folderEntity.getCreatedAt());
		folders.setCreatedBy(folderEntity.getCreatedBy());
		folders.setParentFolderId(String.valueOf(folderEntity.getParentFolderID()));
		folders.setIsCheckIn(folderEntity.getCheckIn());

		if (folderEntity.getCheckIn().equalsIgnoreCase("NO")) {
			folders.setIsCheckOption("YES");
		} else if (andOutEntity != null && folderEntity.getCheckIn().equalsIgnoreCase("YES")
				&& userId == andOutEntity.getUserId()) {
			folders.setIsCheckOption("YES");
		} else {
			folders.setIsCheckOption("NO");
		}

		if (andOutEntity != null && folderEntity.getCheckIn().equalsIgnoreCase("YES")
				&& userId == andOutEntity.getUserId()) {
			folders.setCheckIn("YES");
		} else if (folderEntity.getCheckOut().equalsIgnoreCase("YES")) {
			folders.setCheckIn("YES");
		} else {
			folders.setCheckIn("NO");
		}

		if (bookMarkEntity == null) {
			folders.setBookmark("NO");
		} else {
			folders.setBookmark("YES");
		}
		return folders;
	}

	public FolderEntity convertFoToFolderEntity(String prospectId) {
		FolderEntity entity = new FolderEntity();
		entity.setProspectId(prospectId);

		return entity;
	}

	public String storeFolder(FileResponse fileResponse, FolderFO folderFO) {
		String folderPath = folderLocation + folderFO.getFolderName();

		File file = new File(folderPath);

		if (file.exists()) {
			fileResponse.setStatus(DMSConstant.FAILURE);
			fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
		}

		if (file.mkdirs()) {
			logger.info("Directory is created");
		} else {
			logger.info("Failed to create directory");
			fileResponse.setStatus(DMSConstant.FAILURE);
		}

		return folderPath;
	}

	public List<Folders> convertToGetAllFolders(int userId, String userName) {
		// Retrieve user's group mappings
		List<ProfUserGroupMappingEntity> userGroupMappings = groupMappingRepository.findByUserId(userId);
		// Retrieve user's access mappings
		List<ProfAccessUserMappingEntity> userAccessMappings = accessUserMappingRepository
				.findByUserId(String.valueOf(userId));

		// Retrieve user's access group mappings
		List<ProfAccessGroupMappingEntity> groupAccessMappings = new ArrayList<>();
		for (ProfUserGroupMappingEntity userGroupMapping : userGroupMappings) {
			String groupId = userGroupMapping.getGroupId();
			List<ProfAccessGroupMappingEntity> mappings = accessGroupMappingRepository.findByGroupId(groupId);
			groupAccessMappings.addAll(mappings);
		}

		List<Integer> accessIds = new ArrayList<>();

		// Retrieve access IDs from userAccessMappings
		for (ProfAccessUserMappingEntity userAccessMapping : userAccessMappings) {
			ProfAccessRightsEntity accessRightsEntity = userAccessMapping.getAccessRightsEntity();
			if (accessRightsEntity != null) {
				accessIds.add(accessRightsEntity.getId());
			}
		}

		// Retrieve access IDs from groupAccessMappings
		for (ProfAccessGroupMappingEntity groupAccessMapping : groupAccessMappings) {
			ProfAccessRightsEntity accessRightsEntity = groupAccessMapping.getAccessRightsEntity();
			if (accessRightsEntity != null) {
				accessIds.add(accessRightsEntity.getId());
			}
		}
		// Retrieve access rights based on access IDs
		List<ProfAccessRightsEntity> accessRights = accessRightRepository.findByIdIn(accessIds);

		// Retrieve folder entities
		List<FolderEntity> foldersList = folderRepo.findAll(Sort.by(Sort.Direction.ASC, "parentFolderID"));

		int parentFolder = foldersList.get(0).getParentFolderID();
		return foldersList.stream().filter(folderEntity -> parentFolder == folderEntity.getParentFolderID()
				&& folderEntity.getStatus().equalsIgnoreCase("A")).map(folderEntity -> {
					Folders folder = convertFolderEntityToFolder(folderEntity, userName, userId);
					for (ProfAccessRightsEntity accessRight : accessRights) {
						if (folder.getMetaId() != null && folder.getMetaId().equals(accessRight.getMetaId())) {
							folder.setView(accessRight.getView());
							folder.setWrite(accessRight.getWrite());
							break;
						}
					}
					return folder;
				}).collect(Collectors.toList());

	}

	public Folders convertFolderEntityToFolderFo(FolderEntity folderEntity, List<ProfDocEntity> docEntity) {
		Folders folders = new Folders();
		List<Files> files = new ArrayList<>();
		folders.setFolderID(folderEntity.getId());
		folders.setFolderName(folderEntity.getFolderName());
		folders.setIsParent(folderEntity.getIsParent());
		folders.setMetaId(folderEntity.getMetaId());
		folders.setFolderPath(folderEntity.getFolderPath());
		folders.setCreatedAt(folderEntity.getCreatedAt());
		folders.setCreatedBy(folderEntity.getCreatedBy());
		folders.setParentFolderId(String.valueOf(folderEntity.getParentFolderID()));
		for (ProfDocEntity profDocEntity : docEntity) {
			if (profDocEntity.getStatus().equalsIgnoreCase(folderLocation)) {
				ProfFileBookmarkEntity fileBookmarkEntity = fileBookmarkRepository.findByFileId(profDocEntity.getId());
				Files file = new Files();
				file.setDocName(profDocEntity.getDocName());
				file.setFileName(profDocEntity.getDocPath());
				file.setId(profDocEntity.getId());
				file.setCreatedAt(profDocEntity.getUploadTime());
				file.setCreatedBy(profDocEntity.getCreatedBy());
				if (fileBookmarkEntity == null) {
					file.setBookmark("NO");
				} else {
					file.setBookmark("YES");
				}
				files.add(file);
			}
		}
		folders.setFiles(files);
		return folders;
	}

	public ProfFolderRetrieveResponse convertFolderEntityToFolderRetrieveResponse(List<FolderEntity> entity,
			List<ProfAccessRightsEntity> accessRights, String token) {
		ProfFolderRetrieveResponse folderRetrieveResponse = new ProfFolderRetrieveResponse();
		List<FolderPathResponse> folderPathResponses = new ArrayList<>();
		ProfUserPropertiesEntity userPropertiesEntity = profUserPropertiesRepository.findByToken(token);

		for (FolderEntity folderEntity : entity) {
			if (folderEntity.getStatus().equalsIgnoreCase("A")) {
				ProfCheckInAndOutEntity andOutEntity = checkInAndOutRepository.findByFolderIdAndFolderNameAndUserId(
						folderEntity.getId(), folderEntity.getFolderName(), userPropertiesEntity.getUserId());
				ProfFolderBookMarkEntity folderBookMarkEntity = bookmarkRepository.findByFolderId(folderEntity.getId());
				FolderPathResponse folderPathResponse = new FolderPathResponse();
				folderPathResponse.setFolderPath(folderEntity.getFolderPath());
				folderPathResponse.setIsParent(folderEntity.getIsParent());
				folderPathResponse.setCreatedAt(folderEntity.getCreatedAt());
				folderPathResponse.setCreatedBy(folderEntity.getCreatedBy());
				folderPathResponse.setFolderID(String.valueOf(folderEntity.getId()));
				folderPathResponse.setFolderName(folderEntity.getFolderName());
				folderPathResponse.setMetaId(folderEntity.getMetaId());
				folderPathResponse.setIsCheckIn(folderEntity.getCheckIn());

				if (folderEntity.getCheckIn().equalsIgnoreCase("NO")) {
					folderPathResponse.setIsCheckOption("YES");
				} else if (andOutEntity != null && folderEntity.getCheckIn().equalsIgnoreCase("YES")
						&& userPropertiesEntity.getUserId() == andOutEntity.getUserId()) {
					folderPathResponse.setIsCheckOption("YES");
				} else {
					folderPathResponse.setIsCheckOption("NO");
				}

				if (andOutEntity != null && folderEntity.getCheckIn().equalsIgnoreCase("YES")
						&& userPropertiesEntity.getUserId() == andOutEntity.getUserId()) {
					folderPathResponse.setCheckIn("YES");
				} else if (folderEntity.getCheckOut().equalsIgnoreCase("YES")) {
					folderPathResponse.setCheckIn("YES");
				} else {
					folderPathResponse.setCheckIn("NO");
				}

				if (folderBookMarkEntity == null) {
					folderPathResponse.setBookmark("NO");
				} else {
					folderPathResponse.setBookmark("YES");
				}

				// Find the corresponding access rights for this folder
				Optional<ProfAccessRightsEntity> accessRight = accessRights.stream()
						.filter(access -> folderEntity.getMetaId().equals(access.getMetaId())).findFirst();

				// Set view and write access if access rights are present
				accessRight.ifPresent(access -> {
					folderPathResponse.setView(access.getView());
					folderPathResponse.setWrite(access.getWrite());
				});

				folderPathResponses.add(folderPathResponse);
			}
		}
		folderRetrieveResponse.setSubFolderPath(folderPathResponses);
		return folderRetrieveResponse;
	}



}
