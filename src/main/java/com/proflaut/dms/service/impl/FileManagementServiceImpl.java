package com.proflaut.dms.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMailConfigEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfMailConfigRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;
import com.proflaut.dms.util.Compression;

@Service
@Transactional
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
	EntityManager entityManager;

	@Autowired
	ProfMailConfigRepository configRepository;

	@Autowired
	ProfMetaDataRepository metaDataRepository;

	@Autowired
	AccessRightsServiceImpl accessRightsServiceImpl;

	@Autowired
	MetaServiceImpl metaServiceImpl;

	@Value("${create.folderlocation}")
	private String folderLocation;

	@Transactional
	public FileResponse storeFile(FileRequest fileRequest, String token, TransactionStatus status,
			PlatformTransactionManager transactionManager) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
			if (profUserInfoEntity == null) {
				throw new CustomException("ProfUserInfoEntity not found for userId: " + userProp.getUserId());
			}
			if (fileHelper.storeDocument(fileRequest, userProp.getUserId(), profUserInfoEntity.getUserName(), token)) {
				fileResponse.setFolderPath(fileRequest.getDockPath());
				fileResponse.setStatus(DMSConstant.SUCCESS);
				transactionManager.commit(status);
			}

		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new CustomException(e.getMessage());
		}

		return fileResponse;
	}

	public FileRetreiveResponse retreiveFile(String token, String prospectId) throws CustomException {
		FileRetreiveResponse fileRetreiveResponse = new FileRetreiveResponse();
		ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
		ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
		List<ProfDocEntity> profDocEntity = profDocUploadRepository.findByProspectId(prospectId);
		if (profDocEntity == null) {
			throw new CustomException("ProfDocEntity not found for prospectId: " + prospectId);
		}
		String decrypted = null;
		decrypted = fileHelper.retrievDocument(profDocEntity, decrypted, fileRetreiveResponse, infoEntity);
		if (!StringUtils.isEmpty(decrypted)) {
			fileRetreiveResponse.setStatus(DMSConstant.SUCCESS);
		} else {
			fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
		}

		return fileRetreiveResponse;
	}

	public Map<String, Object> reteriveFileById(int id) {
		Map<String, Object> response = new LinkedHashMap<>();
		FileRetreiveByResponse fileRetreiveByResponse = new FileRetreiveByResponse();
		try {
			ProfDocEntity docEntity = profDocUploadRepository.findById(id);
			if (docEntity == null) {
				throw new CustomException("ProfDocEntity not found for ID: " + id);
			}
			FolderEntity entity = folderRepository.findById(docEntity.getFolderId());
			if (entity == null) {
				throw new CustomException("FolderEntity not found for ID: " + docEntity.getFolderId());
			}
			String decrypted = fileHelper.retrieveDocument(docEntity);
			if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
				fileRetreiveByResponse.setImage(decrypted);
				fileRetreiveByResponse.setExtention(docEntity.getExtention());
				fileRetreiveByResponse.setDocName(docEntity.getDocName());
				fileRetreiveByResponse.setStatus(DMSConstant.SUCCESS);
				GetAllTableResponse allTableResponse = metaServiceImpl.getAll(docEntity);
				response.put("image", fileRetreiveByResponse);
				response.put("metaDetails", allTableResponse);
			} else {
				fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
				response.put("image", fileRetreiveByResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
		}
		return response;
	}

	public ProfEmailShareResponse emailReader(ProfEmailShareRequest emailShareRequest) {
		ProfEmailShareResponse emailShareResponse = new ProfEmailShareResponse();
		try {
			ProfDocEntity docEntity = profDocUploadRepository.findById(emailShareRequest.getDocId());
			String extension = docEntity.getExtention();
			if (docEntity.getDocName() != null) {
				MailInfoRequest mailInfoRequest = new MailInfoRequest(emailShareRequest.getFrom(),
						Arrays.asList(emailShareRequest.getTo()), "Subject");
				String path = folderLocation + File.separator + docEntity.getDocPath();
				String content = new String(Files.readAllBytes(Paths.get(path)));
				PasswordEncDecrypt td = new PasswordEncDecrypt();
				String decryptedBase64 = td.decrypt(content);
				String decompressedBytes = Compression.decompressB64(decryptedBase64);
				byte[] fileBytes = Base64.getDecoder().decode(decompressedBytes);
				if (fileHelper.sendMail(mailInfoRequest, fileBytes, extension, docEntity)) {
					ProfMailConfigEntity configEntity = fileHelper.convertemailShareReqToMailConf(emailShareRequest);
					configRepository.save(configEntity);
					docEntity.setEmilResId(String.valueOf(configEntity.getId()));
					profDocUploadRepository.updateEmailResIdAndIsEmail(String.valueOf(configEntity.getId()),"Y", docEntity.getId());
//					profDocUploadRepository.updateIsEmail("Y", docEntity.getId());
					emailShareResponse.setStatus(DMSConstant.MESSAGE);
					emailShareResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					emailShareResponse.setMessage("Failed While Sending Mail");
					emailShareResponse.setStatus(DMSConstant.FAILURE);
				}
			} else {
				throw new CustomException("docName or folderPath is null");
			}

		} catch (CustomException ce) {
			ce.printStackTrace();
			emailShareResponse.setStatus(DMSConstant.FAILURE);
			emailShareResponse.setMessage(ce.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			emailShareResponse.setStatus(DMSConstant.FAILURE);
		}

		return emailShareResponse;
	}

}
