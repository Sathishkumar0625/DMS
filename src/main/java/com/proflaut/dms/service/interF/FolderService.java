package com.proflaut.dms.service.interF;

import java.util.List;

import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderRetreiveResponse;
import com.proflaut.dms.model.Folders;

public interface FolderService {
	
	FileResponse saveFolder(FolderFO folderFO) throws CustomException;
	FileResponse retriveFile(Integer id);
	FolderRetreiveResponse retreive(Integer id);
	FolderRetreiveResponse getAllFolders();
	
}
