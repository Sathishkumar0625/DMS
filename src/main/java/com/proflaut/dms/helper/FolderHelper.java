package com.proflaut.dms.helper;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Component
public class FolderHelper {
	@Autowired
	FolderRepository folderRepository;
	@Value("${create.folderlocation}")
	private String folderLocation;

	@Autowired
	FolderRepository folderRepo;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfMetaDataRepository dataRepository;

	@Autowired
	ProfUserGroupMappingRepository groupMappingRepository;

	@Autowired
	ProfAccessGroupMappingRepository accessGroupMappingRepository;

	@Autowired
	ProfAccessUserMappingRepository accessUserMappingRepository;

	@Autowired
	ProfAccessRightRepository accessRightRepository;

	private static final Logger logger = LogManager.getLogger(FolderHelper.class);

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public FolderEntity convertFOtoBO(FolderFO folderFO, FileResponse fileResponse,
			ProfUserPropertiesEntity propertiesEntity) {

		FolderEntity ent = new FolderEntity();
		String folderPath = "";
		folderPath = storeFolder(fileResponse, folderFO);
		ent.setFolderName(folderFO.getFolderName());
		ent.setFolderPath(folderPath);
		ent.setIsParent(folderLocation);
		ent.setMetaId(folderFO.getMetaDataId());
		ent.setCreatedAt(formatCurrentDateTime());
		ent.setCreatedBy(propertiesEntity.getUserName());
		ent.setParentFolderID(folderFO.getParentFolderID());
		return ent;
	}

	public FolderEntity callFolderEntity(Integer id) {
		return folderRepository.findById(id).get();

	}

	public Folders convertFolderEntityToFolder(FolderEntity folderEntity) {
		Folders folders = new Folders();

		folders.setFolderID(folderEntity.getId());
		folders.setFolderName(folderEntity.getFolderName());
		folders.setIsParent(folderEntity.getIsParent());
		folders.setMetaId(folderEntity.getMetaId());
		folders.setFolderPath(folderEntity.getFolderPath());
		folders.setCreatedAt(folderEntity.getCreatedAt());
		folders.setCreatedBy(folderEntity.getCreatedBy());
		folders.setParentFolderId(folderEntity.getParentFolderID());
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

	public List<Folders> convertToGetAllFolders(int userId) {
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

		return foldersList.stream().map(folderEntity -> {
			Folders folder = convertFolderEntityToFolder(folderEntity);
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

	public Folders convertFolderEntityToFolderFo(FolderEntity folderEntity, ProfDocEntity docEntity) {
		Folders folders=new Folders();
		folders.setFolderID(folderEntity.getId());
		folders.setFolderName(folderEntity.getFolderName());
		folders.setIsParent(folderEntity.getIsParent());
		folders.setMetaId(folderEntity.getMetaId());
		folders.setFolderPath(folderEntity.getFolderPath());
		folders.setCreatedAt(folderEntity.getCreatedAt());
		folders.setCreatedBy(folderEntity.getCreatedBy());
		folders.setParentFolderId(folderEntity.getParentFolderID());
		folders.setFileName(docEntity.getDocPath());
		return folders;
	}

}
