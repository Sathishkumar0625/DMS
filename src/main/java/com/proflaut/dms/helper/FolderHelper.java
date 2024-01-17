package com.proflaut.dms.helper;

import org.springframework.beans.factory.annotation.Autowired;
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
	

	public FolderEntity convertFOtoBO(FolderFO folderFO, FileResponse fileResponse)  {
		
		FolderEntity ent = new FolderEntity();
		ent.setFolderName(folderFO.getFolderName());
		ent.setIsParent(folderFO.getIsParent());
		ent.setParentFolderID(folderFO.getParentFolderID());
		ent.setCustomerId(folderFO.getCustomerId());
		String folderPath="";
		folderPath=userhelper.storeFolder(folderFO.getFolderName(),fileResponse,folderFO);
		ent.setFolderPath(folderPath);
		return ent;
	}
	public FolderEntity callFolderEntity(Integer id) {
		return folderRepository.findById(id).get();

	}
	public Folders convertFolderEntityToFolder(FolderEntity folderEntity) {
		Folders folders=new Folders();
	    folders.setFolderID(folderEntity.getId()); 
	    folders.setFolderName(folderEntity.getFolderName());
	    return folders ;
		
	}
	
}
