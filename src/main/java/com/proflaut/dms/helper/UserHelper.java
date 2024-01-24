package com.proflaut.dms.helper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfAccountDetailsEntity;
import com.proflaut.dms.entity.ProfAccountRequestEntity;
import com.proflaut.dms.entity.ProfActivitiesEntity;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.entity.ProfDmsMainEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfExecutionEntity;
import com.proflaut.dms.entity.ProfOldImageEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.AccountDetailsRequest;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfGetExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.service.impl.FolderServiceImpl;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;
import com.proflaut.dms.util.CompressionUtil;

@Component
public class UserHelper {

	@Value("${upload.filelocation}")
	private String fileLocation;

	@Value("${create.folderlocation}")
	private String folderLocation;

	private final Random random = new Random();

	@Autowired
	ProfOldImageRepository imageRepository;

	@Autowired
	ProfUserInfoRepository userInfoRepository;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfDocUploadRepository docUploadRepository;

	@Autowired
	FolderServiceImpl folderServiceImpl;

	private static final Logger logger = LogManager.getLogger(UserHelper.class);

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public ProfUserInfoEntity convertUserInfotoProfUser(UserInfo userInfo) throws InvalidKeyException,
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		PasswordEncDecrypt td = new PasswordEncDecrypt();
		String encrypted = td.encrypt(userInfo.getPassword());
		logger.info("USER PASSWORD ---> {}", userInfo);
		ProfUserInfoEntity ent = new ProfUserInfoEntity();
		ent.setStatus("A");
		ent.setAdminAccesss(userInfo.getAdminAccess());
		ent.setWebAccess(userInfo.getWebAccess());
		ent.setEmail(userInfo.getEmail());
		ent.setPassword(encrypted);
		ent.setUserName(userInfo.getUserName());
		ent.setCreatedDate(formatCurrentDateTime());
		return ent;
	}

	public boolean validatePassword(ProfUserInfoEntity profUserInfoEntity, UserInfo userInfo)
			throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		PasswordEncDecrypt td = new PasswordEncDecrypt();
		String decryptPassword = td.decrypt(profUserInfoEntity.getPassword());
		boolean isValidate;
		if (decryptPassword.equals(userInfo.getPassword())) {
			isValidate = true;
		} else {
			isValidate = false;
		}
		return isValidate;
	}

	public ProfDocEntity convertFileRequesttoProfDoc(FileRequest fileRequest, String token, FolderEntity entity,
			String fileName) {
		ProfDocEntity ent = new ProfDocEntity();
		ProfUserPropertiesEntity userProp = profUserPropertiesRepository.findByToken(token);
		if (userProp != null) {
			ent.setCreatedBy(userProp.getUserId());
		}
		ent.setFolderId(entity.getId());
		ent.setUploadTime(LocalDateTime.now().toString());
		ent.setProspectId(fileRequest.getProspectId());
		ent.setDocName(fileRequest.getDockName());
		ent.setDocPath(fileName);
		return ent;
	}

	public String tokengenerator() {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGenerator.init(128); // block size is 128bits
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.toString();
	}

	public ProfUserPropertiesEntity convertUserInfotoProfUserProp(ProfUserInfoEntity profUserInfoEntity,
			Map<String, String> tokenResp) {
		ProfUserPropertiesEntity ent = new ProfUserPropertiesEntity();
		ent.setToken(tokenResp.get("token"));
		ent.setSecKey(tokenResp.get("seckey"));
		ent.setUserId(profUserInfoEntity.getUserId());
		String localdateandtime = LocalDateTime.now().toString();
		ent.setLastLogin(localdateandtime);
		return ent;
	}

	@Transactional
	public boolean storeDocument(FileRequest fileRequest, int uId, String uName, String token)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		boolean isFileCreated = false;
		FolderEntity entity = folderRepository.findByProspectId(fileRequest.getProspectId());

		if (entity != null) {
			int count = entity.getParentFolderID();
			count += 1;
			String path = entity.getFolderPath();
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString();

			ProfDocEntity existingDocEntity = docUploadRepository.findByDocNameAndProspectId(fileRequest.getDockName(),
					fileRequest.getProspectId());

			if (existingDocEntity != null) {
				File newFile = new File(path + File.separator + fileName);

				try (FileWriter fileWriter = new FileWriter(newFile)) {
					String compressedBytes = CompressionUtil.compressAndReturnB64(fileRequest.getImage());
					PasswordEncDecrypt td = new PasswordEncDecrypt();
					String encrypted = td.encrypt(compressedBytes);

					fileWriter.write(encrypted);
					isFileCreated = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				ProfOldImageEntity imageEntity = convertFileReqToOldImage(existingDocEntity, uId, uName, fileRequest);	
				imageRepository.save(imageEntity);
				moveDocumentToBackup(existingDocEntity, entity.getProspectId(), entity);
								
				existingDocEntity.setDocPath(fileName);
				docUploadRepository.save(existingDocEntity);
			} else {
				File file = new File(path + File.separator + fileName);
				folderRepository.updateParentFolderIdAndFolderPath(count, entity.getProspectId());

				try (FileWriter fileWriter = new FileWriter(file)) {
					String compressedBytes = CompressionUtil.compressAndReturnB64(fileRequest.getImage());
					PasswordEncDecrypt td = new PasswordEncDecrypt();
					String encrypted = td.encrypt(compressedBytes);

					fileWriter.write(encrypted);
					isFileCreated = true;
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (isFileCreated) {
					ProfDocEntity profDocEnt = convertFileRequesttoProfDoc(fileRequest, token, entity, fileName);
					docUploadRepository.save(profDocEnt);
				}
			}
		}
		return isFileCreated;
	}

	private ProfOldImageEntity convertFileReqToOldImage(ProfDocEntity existingDocEntity, int uId, String uName,
			FileRequest fileRequest) {
		ProfOldImageEntity oldImageEntity = new ProfOldImageEntity();
		oldImageEntity.setDocName(existingDocEntity.getDocName());
		oldImageEntity.setDocPath(existingDocEntity.getDocPath());
		oldImageEntity.setUserName(uName);
		oldImageEntity.setProspectId(fileRequest.getProspectId());
		oldImageEntity.setCreatedBy(String.valueOf(uId));
		return oldImageEntity;
	}

	private void moveDocumentToBackup(ProfDocEntity existingDocEntity, String string, FolderEntity entity) {
		String backupFolderPath = folderLocation + "Backup_" + string;
		Path existingPath = Paths.get(entity.getFolderPath(), existingDocEntity.getDocPath());
		Path backupFolder = Paths.get(backupFolderPath);

		if (!Files.exists(backupFolder)) {
			try {
				Files.createDirectories(backupFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Path backupFilePath = backupFolder.resolve(existingPath.getFileName());
		try {
	        if (Files.exists(backupFilePath)) {
	            Files.copy(existingPath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
	        } else {
	            Files.move(existingPath, backupFilePath, StandardCopyOption.REPLACE_EXISTING);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
	}

//	public String retrievDocument(List<ProfDocEntity> profDocEntity, String decrypted,
//			FileRetreiveResponse fileRetreiveResponse) {
//		try {
//			List<DocumentDetails> document = new ArrayList<>();
//			for (int i = 0; i < profDocEntity.size(); i++) {
//				// handle no such file exception
//				if (!org.springframework.util.StringUtils.isEmpty(profDocEntity.get(i).getDocPath())) {
//					String content = new String(Files.readAllBytes(Paths.get(profDocEntity.get(i).getDocPath())));
//					PasswordEncDecrypt td = new PasswordEncDecrypt();
//					decrypted = td.decrypt(content);
//					if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
//						DocumentDetails documentDetails = new DocumentDetails();
//						documentDetails.setDocId(profDocEntity.get(i).getDocId());
//						documentDetails.setImage(decrypted);
//						documentDetails.setUploadedTime(profDocEntity.get(i).getUploadTime());
//						document.add(i, documentDetails);
//						fileRetreiveResponse.setDocument(document);
//					}
//				} else {
//					fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
//					break;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return decrypted;
//	}

	public String retrievDocument(List<ProfDocEntity> profDocEntity, String decrypted,
			FileRetreiveResponse fileRetreiveResponse, ProfUserInfoEntity infoEntity) {
		List<DocumentDetails> document = new ArrayList<>();

		for (int i = 0; i < profDocEntity.size(); i++) {
			FolderEntity entity = folderRepository.findByProspectId(profDocEntity.get(i).getProspectId());

			if (!org.springframework.util.StringUtils.isEmpty(profDocEntity.get(i).getDocPath())) {
				try {
					String filePath = entity.getFolderPath() + File.separator + profDocEntity.get(i).getDocPath();
					String encryptedContent = new String(Files.readAllBytes(Paths.get(filePath)));
					PasswordEncDecrypt td = new PasswordEncDecrypt();
					decrypted = td.decrypt(encryptedContent);

					if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {

						DocumentDetails documentDetails = new DocumentDetails();
						documentDetails.setProspectId(profDocEntity.get(i).getProspectId());
						documentDetails.setUploadedBy(infoEntity.getUserName());
						documentDetails.setId(profDocEntity.get(i).getId());
						documentDetails.setDocName(profDocEntity.get(i).getDocName());
						documentDetails.setUploadedTime(profDocEntity.get(i).getUploadTime());
						document.add(i, documentDetails);
						fileRetreiveResponse.setDocument(document);
					}
				} catch (Exception e) {
					e.printStackTrace();
					DocumentDetails documentDetails = new DocumentDetails();
					documentDetails.setProspectId(profDocEntity.get(i).getProspectId());
					documentDetails.setDocName(profDocEntity.get(i).getDocName());
					documentDetails.setUploadedTime(profDocEntity.get(i).getUploadTime());
					document.add(i, documentDetails);
					fileRetreiveResponse.setDocument(document);
					fileRetreiveResponse.setStatus(DMSConstant.FILE_NOT_FOUND);
				}
			} else {
				fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
			}
		}
		return decrypted;
	}

	public ProfAccountRequestEntity convertCustomerAcctoProfUser(AccountDetailsRequest accountDetailsRequest) {
		ProfAccountRequestEntity profAccountRequestListEntity = new ProfAccountRequestEntity();
		profAccountRequestListEntity.setCustomerId(accountDetailsRequest.getCustomerId());
		List<ProfAccountDetailsEntity> profAccountDetailsListEntity = new ArrayList<>();
		for (int i = 0; i < accountDetailsRequest.getAccountUserRequest().size(); i++) {
			ProfAccountDetailsEntity profAccountDetailsEntity = new ProfAccountDetailsEntity();
			profAccountDetailsEntity
					.setAccountBranch(accountDetailsRequest.getAccountUserRequest().get(i).getAccountBranch());
			profAccountDetailsEntity
					.setAccountNumber(accountDetailsRequest.getAccountUserRequest().get(i).getAccountNumber());
			profAccountDetailsEntity.setAddress(accountDetailsRequest.getAccountUserRequest().get(i).getAddress());
			profAccountDetailsEntity
					.setBranchCode(accountDetailsRequest.getAccountUserRequest().get(i).getBranchCode());
			profAccountDetailsEntity.setCity(accountDetailsRequest.getAccountUserRequest().get(i).getCity());
			profAccountDetailsEntity.setCustomerId(accountDetailsRequest.getCustomerId());
			profAccountDetailsEntity.setIFSCcode(accountDetailsRequest.getAccountUserRequest().get(i).getIFSCcode());
			profAccountDetailsEntity.setMICRcode(accountDetailsRequest.getAccountUserRequest().get(i).getMICRcode());
			profAccountDetailsEntity.setODlimit(accountDetailsRequest.getAccountUserRequest().get(i).getODlimit());
			profAccountDetailsEntity.setState(accountDetailsRequest.getAccountUserRequest().get(i).getState());
			profAccountDetailsListEntity.add(profAccountDetailsEntity);

		}
		profAccountRequestListEntity.setProfAccountDetailsEntity(profAccountDetailsListEntity);

		return profAccountRequestListEntity;
	}

//	public String storeFolder(String folderName, FileResponse fileResponse, FolderFO folderFO) {
//		FolderEntity entity=folderRepository.findByParentFolderID(folderFO.getParentFolderID());
//	    File file = new File(folderLocation + folderName);
//	    
//	    if (!file.exists()) {
//	        if (file.mkdir()) {
//	            logger.info("Directory is created");
//	        } else {
//	            logger.info("Failed to create directory");
//	            fileResponse.setStatus(DMSConstant.FAILURE);
//	        }
//	    } else {
//	        logger.info("Directory already exists");
//	        fileResponse.setStatus(DMSConstant.FAILURE);
//	        fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
//	    }
//
//	    return file.getAbsolutePath();
//	}

	public String storeFolder(FileResponse fileResponse, FolderFO folderFO) {
		String folderPath = folderLocation + folderFO.getProspectId();

		File file = new File(folderPath);

		if (file.exists()) {
			fileResponse.setStatus(DMSConstant.FAILURE);
			fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
			return folderPath; // Return the existing folder path
		}

		if (file.mkdirs()) {
			logger.info("Directory is created");
		} else {
			logger.info("Failed to create directory");
			fileResponse.setStatus(DMSConstant.FAILURE);
		}

		return folderPath;
	}

	public ProfUserPropertiesEntity callProfUserConnection(String token) {
		return profUserPropertiesRepository.findByToken(token);

	}

	public ProfOldImageEntity moveDetailsToOldImage(ProfDocEntity existingDocEntity) {
		ProfOldImageEntity oldImageEntity = new ProfOldImageEntity();
		oldImageEntity.setDocName(existingDocEntity.getDocName());
		oldImageEntity.setDocPath(existingDocEntity.getDocPath());
		return oldImageEntity;
	}

	private String generateUniqueId() {
		return String.format("%04d", this.random.nextInt(999));
	}

	public ProfActivitiesEntity convertReqtoProfActEnti(ProfActivityRequest activityRequest,
			ProfUserInfoEntity entity) {
		ProfActivitiesEntity activitiesEntity = new ProfActivitiesEntity();
		activitiesEntity.setCreatedAt(LocalDateTime.now().toString());
		activitiesEntity.setGroupId(activityRequest.getGroupId());
		activitiesEntity.setKey(activityRequest.getKey());
		activitiesEntity.setProcessId(activityRequest.getProcessId());
		activitiesEntity.setTitle(activityRequest.getTitle());
		activitiesEntity.setStatus("A");
		activitiesEntity.setUserID(activityRequest.getUserID());
		activitiesEntity.setCreatedBy(entity.getUserName());
		return activitiesEntity;
	}

	public ProfDmsMainEntity convertMakerReqToMakerEntity(ProfDmsMainRequest mainRequest) throws CustomException {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setAccountNo(mainRequest.getAccountNo());
		mainEntity.setBranchcode(mainRequest.getBranchcode());
		mainEntity.setBranchName(mainRequest.getBranchName());
		mainEntity.setCustomerId(mainRequest.getCustomerId());
		mainEntity.setIfsc(mainRequest.getIfsc());
		mainEntity.setName(mainRequest.getName());
		mainEntity.setUserId(mainRequest.getUserId());
		mainEntity.setKey(mainRequest.getKey());
		String uniqueId = generateUniqueId();
		mainEntity.setProspectId("DMS_" + uniqueId);
		FolderFO folderFO = new FolderFO();
		folderFO.setProspectId("DMS_" + uniqueId);
		folderServiceImpl.saveFolder(folderFO);
		return mainEntity;
	}

	public ProfDmsMainReterive convertMainEntityToMainReterive(ProfDmsMainEntity dmsMainEntity) {
		ProfDmsMainReterive dmsMainReterive = new ProfDmsMainReterive();
		dmsMainReterive.setAccountNumber(dmsMainEntity.getAccountNo());
		dmsMainReterive.setBranchCode(dmsMainEntity.getBranchcode());
		dmsMainReterive.setBranchName(dmsMainEntity.getBranchName());
		dmsMainReterive.setCustomerId(dmsMainEntity.getCustomerId());
		dmsMainReterive.setIfsc(dmsMainEntity.getIfsc());
		dmsMainReterive.setName(dmsMainEntity.getName());
		dmsMainReterive.setProspectId(dmsMainEntity.getProspectId());
		return dmsMainReterive;
	}

	public ProfDmsMainEntity convertUpdateDmsReqToDmsEntity(ProfUpdateDmsMainRequest dmsMainRequest,
			ProfDmsMainEntity mainEntity) {
		mainEntity.setAccountNo(dmsMainRequest.getAccountNo());
		mainEntity.setBranchcode(dmsMainRequest.getBranchCode());
		mainEntity.setCustomerId(dmsMainRequest.getCustomerId());
		mainEntity.setIfsc(dmsMainRequest.getIfsc());
		mainEntity.setName(dmsMainRequest.getName());
		mainEntity.setBranchName(dmsMainRequest.getBranch());
		return mainEntity;
	}

	public ProfDmsHeader convertjsontoHeaderEntity(String jsonData) {
		ProfDmsHeader dmsHeader = new ProfDmsHeader();
		dmsHeader.setKey("checker");
		dmsHeader.setFields(convertToJsonString(jsonData));
		return dmsHeader;
	}

	private String convertToJsonString(String jsonData) {
		try {
			jsonData = jsonData.replace("\r", "").replace("\n", "");
			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			return "Something Went Wrong";
		}
	}



	public String retrievDocument(ProfDocEntity docEntity, FolderEntity entity) {
		String decompressedBytes = "";
		String decrypted = "";
		String path = entity.getFolderPath() + File.separator + docEntity.getDocPath();

		try {
			if (path != null) {
				logger.info("File path -> {} ", path);

				if (!Files.exists(Paths.get(path))) {
					logger.info("File does not exist");
					return decrypted;
				}

				String content = new String(Files.readAllBytes(Paths.get(path)));
				PasswordEncDecrypt td = new PasswordEncDecrypt();
				decrypted = td.decrypt(content);

				decompressedBytes = CompressionUtil.decompressB64(decrypted);
			}
		} catch (AccessDeniedException e) {
			logger.info("Access denied -> {} ", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decompressedBytes;
	}

	public ProfDmsMainEntity convertRequestToProfMain(int userId, String activityName,
			ProfExecutionEntity executionEntity) {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setKey(activityName);
		mainEntity.setUserId(userId);
		mainEntity.setProspectId(executionEntity.getProspectId());
		return mainEntity;
	}

	public ProfExecutionEntity convertRequestToProfHeader(String activityName, ProfUserInfoEntity entity) {
		ProfExecutionEntity executionEntity = new ProfExecutionEntity();
		executionEntity.setActionBy(entity.getUserName());
		executionEntity.setActivityName(activityName);
		executionEntity.setEntryDate(LocalDateTime.now().toString());
		String uniqueId = generateUniqueId();
		executionEntity.setProspectId("DMS_" + uniqueId);
		executionEntity.setStatus("IN PROGRESS");
		return executionEntity;
	}

	public ProfGetExecutionResponse convertExecutionToGetExecution(ProfExecutionEntity profExecutionEntity) {
		ProfGetExecutionResponse executionResponse = new ProfGetExecutionResponse();
		executionResponse.setKey(profExecutionEntity.getActivityName());
		executionResponse.setProspectId(profExecutionEntity.getProspectId());
		return executionResponse;
	}

	public boolean usernameExists(String userName) {
		ProfUserInfoEntity entity = userInfoRepository.findByUserName(userName);
		return entity != null;
	}
}
