package com.proflaut.dms.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.UserHelper;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;

@Service
public class FileManagementServiceImpl {
	@Autowired
	ProfDocUploadRepository profDocUploadRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfUserInfoRepository profUserInfoRepository;

	@Autowired
	UserHelper helper;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfOldImageRepository imageRepository;

	private static final Logger logger = LogManager.getLogger(FileManagementServiceImpl.class);

	public FileResponse storeFile(FileRequest fileRequest, String token) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			PasswordEncDecrypt td = new PasswordEncDecrypt();
			String encrypted = td.encrypt(fileRequest.getImage());
			logger.info("Encrypted File Value ---> {}", encrypted);
			ProfUserPropertiesEntity userProp = helper.callProfUserConnection(token);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());

			if (helper.storeDocument(fileRequest, encrypted, userProp.getUserId(), profUserInfoEntity.getUserName(),
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
		ProfUserPropertiesEntity userProp = helper.callProfUserConnection(token);
		ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
		if (userProp != null) {
			List<ProfDocEntity> profDocEntity = profDocUploadRepository.findByProspectId(prospectId);
			if (profDocEntity != null) {
				String decrypted = null;
				decrypted = helper.retrievDocument(profDocEntity, decrypted, fileRetreiveResponse, infoEntity);
				if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
					fileRetreiveResponse.setStatus(DMSConstant.SUCCESS);
				}
			} else {
				fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
			}
		}

		return fileRetreiveResponse;
	}

	@SuppressWarnings("unused")
	public FileRetreiveByResponse reteriveFileByNameAndId(int id) {
		FileRetreiveByResponse fileRetreiveByResponse = new FileRetreiveByResponse();
		try {
			ProfDocEntity docEntity = profDocUploadRepository.findById(id);
			FolderEntity entity = folderRepository.findById(docEntity.getFolderId());
			if (docEntity != null) {
				String decrypted = helper.retrievDocument(docEntity, entity);
				if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
					fileRetreiveByResponse.setImage(decrypted);
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
}
