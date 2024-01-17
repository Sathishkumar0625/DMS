package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.customException.CustomExcep;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.FolderHelper;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderRetreiveResponse;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.service.interF.FolderService;

@Service
public class FolderServiceImpl implements FolderService {

	@Autowired
	FolderRepository folderRepo;

	@Autowired
	FolderHelper helper;
	
	@Autowired
	FolderService folderService;
	
	public FileResponse saveFolder(FolderFO folderFO) throws CustomException  {
		FileResponse fileResponse = new FileResponse();
		try {
			
				FolderEntity folderEnt = helper.convertFOtoBO(folderFO,fileResponse);
				FolderEntity folderRespEnt = folderRepo.save(folderEnt);
				if (!fileResponse.equals(DMSConstant.FAILURE)) {
					fileResponse.setDocId(folderRespEnt.getId());
					fileResponse.setFolderPath(folderRespEnt.getFolderPath());
					fileResponse.setStatus(DMSConstant.SUCCESS);
				}else {
					fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
					fileResponse.setFolderPath(folderRespEnt.getFolderPath());
					fileResponse.setStatus(DMSConstant.FAILURE);
					
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}
		return fileResponse;
	}
	
	public FileResponse retriveFile(Integer id)  {
		FileResponse fileResponse=new FileResponse();
		try {
			FolderEntity folderEntity=folderRepo.findById(id).get();
			if (folderEntity != null) {
				fileResponse.setStatus(DMSConstant.SUCCESS);
				fileResponse.setDocId(id);
				fileResponse.setFolderPath(folderEntity.getFolderPath());
			}else {
				fileResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileResponse;
	}
	
	public FolderRetreiveResponse retreive(Integer id) {
		FolderRetreiveResponse folderRetreiveResponse = new FolderRetreiveResponse();
		try {
		
		FolderEntity folderEntity = helper.callFolderEntity(id);
		if (folderEntity != null) {
            List<Folders> foldersList = new ArrayList<>();
            Folders folders = helper.convertFolderEntityToFolder(folderEntity);
            
            foldersList.add(folders); 
            folderRetreiveResponse.setStatus(DMSConstant.SUCCESS);
            folderRetreiveResponse.setFolder(foldersList);
        } else {
            folderRetreiveResponse.setStatus(DMSConstant.FAILURE);
        }
    } catch (Exception e) {
        e.printStackTrace();
       
    }
    return folderRetreiveResponse;

	}

	
	public FolderRetreiveResponse getAllFolders() {
	    FolderRetreiveResponse folderRetreiveResponse = new FolderRetreiveResponse();
	    try {
	        List<FolderEntity> foldersList = folderRepo.findAll();
	        List<Folders> folders = foldersList.stream()
	                .map(helper::convertFolderEntityToFolder)
	                .collect(Collectors.toList());

	        folderRetreiveResponse.setStatus(DMSConstant.SUCCESS);
	        folderRetreiveResponse.setFolder(folders);
	    } catch (Exception e) {
	        e.printStackTrace();
	        folderRetreiveResponse.setStatus(DMSConstant.FAILURE);
	    }
	    return folderRetreiveResponse;
	}

}
