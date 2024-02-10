package com.proflaut.dms.helper;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.repository.FolderRepository;

@Component
public class FolderHelper {
	@Autowired
	FolderRepository folderRepository;
	@Value("${create.folderlocation}")
	private String folderLocation;

	@Autowired
	FolderRepository folderRepo;
	
	private static final Logger logger = LogManager.getLogger(FolderHelper.class);
	
	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public FolderEntity convertFOtoBO(FolderFO folderFO, FileResponse fileResponse, ProfUserPropertiesEntity propertiesEntity) {

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


}
