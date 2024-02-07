package com.proflaut.dms.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMailConfigEntity;
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
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
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
	private EntityManager entityManager;

	@Autowired
	ProfMailConfigRepository configRepository;

	@Autowired
	ProfMetaDataRepository metaDataRepository;

	public FileResponse storeFile(FileRequest fileRequest, String token) throws CustomException {
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
			}

		} catch (Exception e) {
			e.printStackTrace();
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

	public FileRetreiveByResponse reteriveFileByNameAndId(int id) {
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
			String decrypted = fileHelper.retrieveDocument(docEntity, entity);
			if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
				fileRetreiveByResponse.setImage(decrypted);
				fileRetreiveByResponse.setExtention(docEntity.getExtention());
				fileRetreiveByResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fileRetreiveByResponse.setStatus(DMSConstant.FAILURE);
		}
		return fileRetreiveByResponse;
	}

	public ProfMetaDataResponse createTableFromFieldDefinitions(CreateTableRequest createTableRequest, String token) {
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
			ProfUserInfoEntity infoEntity = profUserInfoRepository.findByUserId(entity.getUserId());

			if (entity.getToken() != null && infoEntity.getUserName() != null) {
				String tableName = fileHelper.createTable(createTableRequest.getFields(), createTableRequest);
				ProfMetaDataEntity dataEntity = fileHelper.convertTableReqToMetaEntity(createTableRequest, tableName,
						infoEntity);
				if (dataEntity != null) {
					entityManager.persist(dataEntity);
					metaDataResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					throw new CustomException("ProfMetaDataEntity is Null");
				}
			} else {
				metaDataResponse.setStatus(DMSConstant.FAILURE);
				throw new CustomException("Token or userName is null");
			}
		} catch (CustomException ce) {
			ce.printStackTrace();
			metaDataResponse.setStatus(DMSConstant.FAILURE);
			metaDataResponse.setErrorMessage(ce.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			metaDataResponse.setStatus(DMSConstant.FAILURE);
			metaDataResponse.setErrorMessage("An error occurred");
		}
		return metaDataResponse;
	}

	public ProfEmailShareResponse emailReader(ProfEmailShareRequest emailShareRequest) {
		ProfEmailShareResponse emailShareResponse = new ProfEmailShareResponse();
		try {
			FolderEntity folderEntity = folderRepository.findByProspectId(emailShareRequest.getProspectId());
			ProfDocEntity docEntity = profDocUploadRepository.findByDocNameAndProspectId(emailShareRequest.getDocName(),
					folderEntity.getProspectId());
			String extension = docEntity.getExtention();
			if (docEntity.getDocName() != null && folderEntity.getFolderPath() != null) {
				MailInfoRequest mailInfoRequest = new MailInfoRequest(emailShareRequest.getFrom(),
						Arrays.asList(emailShareRequest.getTo()), "Subject");
				String path = folderEntity.getFolderPath() + File.separator + docEntity.getDocPath();
				String content = new String(Files.readAllBytes(Paths.get(path)));
				PasswordEncDecrypt td = new PasswordEncDecrypt();
				String decryptedBase64 = td.decrypt(content);
				String decompressedBytes = Compression.decompressB64(decryptedBase64);
				byte[] fileBytes = Base64.getDecoder().decode(decompressedBytes);
				if (fileHelper.sendMail(mailInfoRequest, fileBytes, extension, docEntity)) {
					ProfMailConfigEntity configEntity = fileHelper.convertemailShareReqToMailConf(emailShareRequest);
					configRepository.save(configEntity);
					docEntity.setEmilResId(String.valueOf(configEntity.getId()));
					profDocUploadRepository.updateEmailResId(String.valueOf(configEntity.getId()), docEntity.getId());

					profDocUploadRepository.updateIsEmail("Y", docEntity.getId());
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

	public GetAllTableResponse getAll(String name) {
		GetAllTableResponse getAllTableResponse = new GetAllTableResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findByNameIgnoreCase(name);
			if (dataEntity != null) {
				getAllTableResponse = fileHelper.convertEntityToResponse(dataEntity, entityManager);
			} else {
				throw new CustomException("DataEntity not found for name: " + name);
			}
		} catch (CustomException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (entityManager != null && entityManager.isOpen()) {
				entityManager.close();
			}
		}
		return getAllTableResponse;
	}

	public ProfMetaDataResponse save(CreateTableRequest createTableRequest, Integer id, FileRequest fileRequest) {
		ProfMetaDataResponse dataResponse = new ProfMetaDataResponse();
		try {
			ProfMetaDataEntity dataEntity = metaDataRepository.findByIdAndNameIgnoreCase(Integer.valueOf(createTableRequest.getMetadataId()),createTableRequest.getTableName());
			Optional<FolderEntity> entity= folderRepository.findById(Integer.valueOf( fileRequest.getFolderId()));
			if (dataEntity != null && !entity.isEmpty()) {
				dataResponse = fileHelper.insertDataIntoTable(dataEntity.getTableName(), createTableRequest.getFields(),id);
			}else {
				throw new CustomException("ID NOT FOUND");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataResponse;
	}
	public List<ProfOverallMetaDataResponse> getAllData() {
	    List<ProfOverallMetaDataResponse> metaResponses = new ArrayList<>();
	    try {
	        List<ProfMetaDataEntity> dataEntities = metaDataRepository.findAll();

	        for (ProfMetaDataEntity metaDataEntity : dataEntities) {
	            if (!metaDataEntity.getStatus().equalsIgnoreCase("I")) {
	                ProfOverallMetaDataResponse response = fileHelper.convertToResponse(metaDataEntity);
	                metaResponses.add(response);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return metaResponses;
	}

}
