package com.proflaut.dms.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.repository.FolderRepository;

@Component
public class FolderHelper {
	@Autowired
	UserHelper userhelper;
	@Autowired
	FolderRepository folderRepository;
	@Value("${create.folderlocation}")
	private String folderLocation;

	@Autowired
	FolderRepository folderRepo;

	public FolderEntity convertFOtoBO(FolderFO folderFO, FileResponse fileResponse) {

		FolderEntity ent = new FolderEntity();
		ent.setProspectId(folderFO.getProspectId());
		ent.setIsParent(folderLocation);
		String folderPath = "";
		folderPath = userhelper.storeFolder(fileResponse, folderFO);
		ent.setFolderPath(folderPath);
		return ent;
	}	

	public FolderEntity callFolderEntity(Integer id) {
		return folderRepository.findById(id).get();

	}

	public Folders convertFolderEntityToFolder(FolderEntity folderEntity) {
		Folders folders = new Folders();
		folders.setFolderID(folderEntity.getId());
		folders.setFolderName(folderEntity.getProspectId());
		return folders;

	}

	public FolderEntity convertFoToFolderEntity(String prospectId) {
		FolderEntity entity = new FolderEntity();
		entity.setProspectId(prospectId);

		return entity;
	}

}
