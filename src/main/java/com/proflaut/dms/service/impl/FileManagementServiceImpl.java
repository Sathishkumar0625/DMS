package com.proflaut.dms.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class FileManagementServiceImpl {
	@Autowired
	ProfDocUploadRepository profDocUploadRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfUserInfoRepository profUserInfoRepository;

	@Autowired
	FileHelper fileHelper;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfOldImageRepository imageRepository;

	@PersistenceContext
	private EntityManager entityManager;


	public FileResponse storeFile(FileRequest fileRequest, String token) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
			
			if (fileHelper.storeDocument(fileRequest, userProp.getUserId(), profUserInfoEntity.getUserName(),
					token)) {
				fileResponse.setFolderPath(fileRequest.getDockPath());
				fileResponse.setStatus(DMSConstant.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(e.getMessage());
		}

		return fileResponse;
	}

	public FileRetreiveResponse retreiveFile(String token, String prospectId) {
		FileRetreiveResponse fileRetreiveResponse = new FileRetreiveResponse();
		ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
		ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
		if (userProp != null) {
			List<ProfDocEntity> profDocEntity = profDocUploadRepository.findByProspectId(prospectId);
			if (profDocEntity != null) {
				String decrypted = null;
				decrypted = fileHelper.retrievDocument(profDocEntity, decrypted, fileRetreiveResponse, infoEntity);
				if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
					fileRetreiveResponse.setStatus(DMSConstant.SUCCESS);
				}
			} else {
				fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
			}
		}

		return fileRetreiveResponse;
	}


	public FileRetreiveByResponse reteriveFileByNameAndId(int id) {
	    FileRetreiveByResponse fileRetreiveByResponse = new FileRetreiveByResponse();
	    try {
	        ProfDocEntity docEntity = profDocUploadRepository.findById(id);
	        if (docEntity != null) {
	            FolderEntity entity = folderRepository.findById(docEntity.getFolderId());
	            String decrypted = fileHelper.retrieveDocument(docEntity, entity);
	            
	            if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
	                fileRetreiveByResponse.setImage(decrypted);
	                fileRetreiveByResponse.setExtention(docEntity.getExtention());
	                fileRetreiveByResponse.setStatus(DMSConstant.SUCCESS);
	            } else {
	                fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
	            }
	        } else {
	            fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
	    }
	    return fileRetreiveByResponse;
	}
	
	@Transactional
	public ProfMetaDataResponse createTableFromFieldDefinitions(CreateTableRequest createTableRequest) {
		ProfMetaDataResponse metaDataResponse=new ProfMetaDataResponse();
	    try {
	    		String tableName=fileHelper.createTable(createTableRequest.getFields(),createTableRequest);
	    		ProfMetaDataEntity dataEntity=fileHelper.convertTableReqToMetaEntity(createTableRequest,tableName);
		        entityManager.persist(dataEntity);
		        metaDataResponse.setStatus(DMSConstant.SUCCESS);
			
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return metaDataResponse;
	}

}
