package com.proflaut.dms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfMailConfigEntity;
import com.proflaut.dms.entity.ProfMountPointFolderMappingEntity;
import com.proflaut.dms.entity.ProfUserGroupMappingEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.Groups;
import com.proflaut.dms.model.ImageRequest;
import com.proflaut.dms.model.ImageResponse;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfOverallCountResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfGroupInfoRepository;
import com.proflaut.dms.repository.ProfMailConfigRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfMountFolderMappingRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
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

	@Autowired
	ProfMountFolderMappingRepository folderMappingRepository;

	@Autowired
	ProfGroupInfoRepository groupInfoRepository;

	@Autowired
	ProfUserGroupMappingRepository groupMappingRepository;

	@Autowired
	RestTemplate restTemplatel;

	@Value("${create.folderlocation}")
	private String folderLocation;

	@Transactional
	public FileResponse storeFile(FileRequest fileRequest, String token, TransactionStatus status,
			PlatformTransactionManager transactionManager) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());
			ProfMountPointFolderMappingEntity entity = folderMappingRepository
					.findByFolderId(Integer.parseInt(fileRequest.getFolderId()));
			if (profUserInfoEntity == null) {
				throw new CustomException("ProfUserInfoEntity not found for userId: " + userProp.getUserId());
			}
			if (fileHelper.storeDocument(fileRequest, userProp.getUserId(), profUserInfoEntity.getUserName(), token,
					entity)) {
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
					profDocUploadRepository.updateEmailResIdAndIsEmail(String.valueOf(configEntity.getId()), "Y",
							docEntity.getId());
					emailShareResponse.setStatus(DMSConstant.MESSAGE);
					emailShareResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					emailShareResponse.setMessage("Failed While Sending Mail");
					emailShareResponse.setStatus(DMSConstant.FAILURE);
				}
			} else {
				throw new CustomException("docName is Null");
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

	public ProfOverallCountResponse reteriveCount() {
		ProfOverallCountResponse countResponse = new ProfOverallCountResponse();
		try {

			// overall File Size
			List<ProfDocEntity> docEntities = profDocUploadRepository.findAll();
			long totalFileSize = fileHelper.getTotalFileSize(docEntities);
			countResponse.setFileSizeCount(totalFileSize + "kb");
			// Overall User Count
			long infoEntity = profUserInfoRepository.count();
			countResponse.setUserCount(String.valueOf(infoEntity));
			// Overall Group Count
			long groupInfoEntities = groupInfoRepository.count();
			countResponse.setGroupCount(String.valueOf(groupInfoEntities));

			// User File Size
			List<ProfUserInfoEntity> entity = profUserInfoRepository.findAll();
			List<String> userNames = entity.stream().map(ProfUserInfoEntity::getUserName).collect(Collectors.toList());
			List<ProfDocEntity> entities = profDocUploadRepository.findByCreatedByIn(userNames);
			long totaluserFileSize = fileHelper.getTotalFileSize(entities);
			countResponse.setUserFileSize(totaluserFileSize + "kb");

			// Group Upload File Size
			List<ProfUserGroupMappingEntity> groupMappingEntities = groupMappingRepository.findAll();
			List<Integer> users = groupMappingEntities.stream().map(ProfUserGroupMappingEntity::getUserId)
					.collect(Collectors.toList());
			List<ProfUserInfoEntity> infoEntities = profUserInfoRepository.findByUserIdIn(users);
			List<String> userNamesInGroup = infoEntities.stream().map(ProfUserInfoEntity::getUserName)
					.collect(Collectors.toList());
			List<ProfDocEntity> entitiesInGroup = profDocUploadRepository.findByCreatedByIn(userNamesInGroup);
			long totalGroupFileSize = fileHelper.getTotalFileSize(entitiesInGroup);
			countResponse.setGroupFileSize(totalGroupFileSize + "kb");

			// get User Group List
			List<ProfGroupInfoEntity> profGroupInfoEntities = groupInfoRepository.findAll();
			List<Groups> groups = new ArrayList<>();
			for (ProfGroupInfoEntity groupInfoEntity : profGroupInfoEntities) {
				List<Groups> updatedGroups = fileHelper.getGroupInfo(groupInfoEntity, groups);
				countResponse.setGroups(updatedGroups);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return countResponse;
	}

	public ImageResponse getImage(ImageRequest imageRequest) {
		ImageResponse imageResponse = new ImageResponse();
		try {
			byte[] byteArray = Base64Utils.decodeFromString(imageRequest.getImage());
			// Set headers
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

			// Create request entity with image file
			HttpEntity<Resource> requestEntity = new HttpEntity<>(new ByteArrayResource(byteArray), headers);

			// Send POST request to Python server
			ResponseEntity<ImageResponse> responseEntity = restTemplatel.exchange("http://127.0.0.1:5000/processImage",
					HttpMethod.POST, requestEntity, ImageResponse.class);

			imageResponse = responseEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageResponse;
	}

}
