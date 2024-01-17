package com.proflaut.dms.helper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.proflaut.dms.model.AccountDetailsRequest;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;

@Component
public class UserHelper {

	@Value("${upload.filelocation}")
	private String fileLocation;

	@Value("${create.folderlocation}")
	private String folderLocation;

	private final Random random = new Random();

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;
	

	private static final Logger logger = LogManager.getLogger(UserHelper.class);

	public ProfUserInfoEntity convertUserInfotoProfUser(UserInfo userInfo) throws InvalidKeyException,
			UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		PasswordEncDecrypt td = new PasswordEncDecrypt();
		String encrypted = td.encrypt(userInfo.getPassword());
		logger.info("USER PASSWORD ---> {}", userInfo);
		ProfUserInfoEntity ent = new ProfUserInfoEntity();
		ent.setEmail(userInfo.getEmail());
		ent.setPassword(encrypted);
		ent.setUserName(userInfo.getUserName());
		ent.setCreatedDate(userInfo.getCreatedDate());
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

	public ProfDocEntity convertFileRequesttoProfDoc(FileRequest fileRequest, String token) throws Exception {
		ProfDocEntity ent = new ProfDocEntity();
		ProfUserPropertiesEntity userProp = profUserPropertiesRepository.findByToken(token);
		if (userProp != null) {
			ent.setCreatedBy(userProp.getUserId());
		}
		ent.setUploadTime(LocalDateTime.now().toString());
		ent.setDocId(fileRequest.getDocId());
		ent.setDocName(fileRequest.getDockName());
		ent.setDocPath(fileRequest.getDockPath());
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

//	public boolean storeDocument(FileRequest fileRequest, String encrypted, int uId, String uName) {
//		boolean isFileCreated = false;
//
//		try {
//			UUID uuid = UUID.randomUUID();
//			String fileName = uuid.toString();
//			String path = uId + "_" + uName + File.separator + fileName;
//			String targetPath=uId+"_"+uName+File.separator+ "_Backup";
//			
//			if (path.isEmpty()) {
//				File file = new File(fileLocation + path);
//				file.getParentFile().mkdirs();
//
//				if (!file.exists() && (file.createNewFile())) {
//					logger.info("File Created");
//				}
//
//				FileWriter fw = new FileWriter(file.getAbsoluteFile());
//				try (BufferedWriter bw = new BufferedWriter(fw)) {
//					bw.write(encrypted);
//					fileRequest.setDockPath(file.getAbsoluteFile().toString());
//					isFileCreated = true;
//				}
//			}else {
//				Files.move(Paths.get(path), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
//			}
//
////			File file = new File(fileLocation + path);
////			file.getParentFile().mkdirs();
////
////			if (!file.exists() && (file.createNewFile())) {
////				logger.info("File Created");
////			}
////
////			FileWriter fw = new FileWriter(file.getAbsoluteFile());
////			try (BufferedWriter bw = new BufferedWriter(fw)) {
////				bw.write(encrypted);
////				fileRequest.setDockPath(file.getAbsoluteFile().toString());
////				isFileCreated = true;
////			}
//		} catch (IOException iox) {
//			iox.printStackTrace();
//		}
//		return isFileCreated;
//	}

	public boolean storeDocument(FileRequest fileRequest, String encrypted, int uId, String uName) {
		boolean isFileCreated = false;

		try {
			UUID uuid = UUID.randomUUID();
			String fileName = uuid.toString();
			String path = uId + "_" + uName + File.separator + fileName;
			String targetPath = uId + "_" + uName + "_Backup" + File.separator + fileName;

			File file = new File(fileLocation + path);

			if (!file.exists()) {
				file.getParentFile().mkdirs();
				Files.write(file.toPath(), encrypted.getBytes());
				fileRequest.setDockPath(file.getAbsolutePath());
				isFileCreated = true;
			} else {
				// Move the existing file to the backup directory
				Files.move(file.toPath(), Paths.get(targetPath, fileName), StandardCopyOption.REPLACE_EXISTING);

				// Create a new file
				File newFile = new File(fileLocation + path);
				newFile.getParentFile().mkdirs();
				Files.write(newFile.toPath(), encrypted.getBytes());
				fileRequest.setDockPath(newFile.getAbsolutePath());
				isFileCreated = true;
			}
		} catch (IOException iox) {
			iox.printStackTrace();
		}
		return isFileCreated;
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
			FileRetreiveResponse fileRetreiveResponse) {
		List<DocumentDetails> document = new ArrayList<>();
		for (int i = 0; i < profDocEntity.size(); i++) {
			if (!org.springframework.util.StringUtils.isEmpty(profDocEntity.get(i).getDocPath())) {
				try {
					String content = new String(Files.readAllBytes(Paths.get(profDocEntity.get(i).getDocPath())));
					PasswordEncDecrypt td = new PasswordEncDecrypt();
					decrypted = td.decrypt(content);
					if (!org.springframework.util.StringUtils.isEmpty(decrypted)) {
						DocumentDetails documentDetails = new DocumentDetails();
						documentDetails.setDocId(profDocEntity.get(i).getDocId());
						//documentDetails.setImage(decrypted);
						documentDetails.setDocName(profDocEntity.get(i).getDocName());
						documentDetails.setUploadedTime(profDocEntity.get(i).getUploadTime());
						document.add(i, documentDetails);
						fileRetreiveResponse.setDocument(document);
					}
				} catch (Exception e) {
					e.printStackTrace();
					DocumentDetails documentDetails = new DocumentDetails();
					documentDetails.setDocId(profDocEntity.get(i).getDocId());
					//documentDetails.setImage(decrypted);
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

	public String storeFolder(String folderName, FileResponse fileResponse, FolderFO folderFO) {
		String folderPath = folderLocation + "customerId_" + folderFO.getCustomerId() + "_parentFolderID_"
				+ folderFO.getParentFolderID() + "/" + folderName;

		File file = new File(folderPath);

		if (file.exists()) {
			logger.info("Directory already exists for parentFolderID {} and customerId {}",
					folderFO.getParentFolderID(), folderFO.getCustomerId());
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

	public ProfOldImageEntity moveDetailsToOldImage(ProfDocEntity existingDocEntity, FileRequest fileRequest) {
		ProfOldImageEntity oldImageEntity = new ProfOldImageEntity();
		oldImageEntity.setDocName(existingDocEntity.getDocName());
		oldImageEntity.setDocPath(existingDocEntity.getDocPath());
		//oldImageEntity.setUserName(existingDocEntity.getUserName());
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

	public ProfDmsMainEntity convertMakerReqToMakerEntity(ProfDmsMainRequest mainRequest) {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setAccountNo(mainRequest.getAccountNo());
		mainEntity.setBranchcode(mainRequest.getBranchcode());
		mainEntity.setBranchName(mainRequest.getBranchName());
		mainEntity.setCustomerId(mainRequest.getCustomerId());
		mainEntity.setIfsc(mainRequest.getIfsc());
		mainEntity.setName(mainRequest.getName());
		mainEntity.setUserId(mainRequest.getUserId());
		mainEntity.setKey(mainRequest.getKey());
		String uniqueId=generateUniqueId();
		mainEntity.setProspectId("DMS_"+uniqueId);
		return mainEntity;
	}

	public ProfDmsMainReterive convertMainEntityToMainReterive(ProfDmsMainEntity dmsMainEntity) {
		ProfDmsMainReterive dmsMainReterive = new ProfDmsMainReterive();
		dmsMainReterive.setAccountNo(dmsMainEntity.getAccountNo());
		dmsMainReterive.setBranchcode(dmsMainEntity.getBranchcode());
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

	public ProfDmsHeader convertjsontoHeaderEntity( String jsonData) {
		ProfDmsHeader dmsHeader=new ProfDmsHeader();
		dmsHeader.setKey("maker");
		dmsHeader.setFields(convertToJsonString(jsonData));
		return dmsHeader;
	}

	private String convertToJsonString(String jsonData) {
		try {
			jsonData = jsonData.replace("\r","").replace("\n","");
	        return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			return "Something Went Wrong";
		}
	}

	public String retrievDocument(ProfDocEntity docEntity, String decrypted) {
		String path=docEntity.getDocPath();
		try {
			if (path != null) {
				String content = new String(Files.readAllBytes(Paths.get(path)));
				PasswordEncDecrypt td = new PasswordEncDecrypt();
				decrypted = td.decrypt(content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}

	public ProfDmsMainEntity convertRequestToProfMain(int userId, String activityName, ProfExecutionEntity executionEntity) {
		ProfDmsMainEntity mainEntity=new ProfDmsMainEntity();
		mainEntity.setAccountNo("12345678");
		mainEntity.setUserId(userId);
		mainEntity.setBranchcode("1406");
		mainEntity.setCustomerId(1);
		mainEntity.setIfsc("IOB12345");
		mainEntity.setKey(activityName);
		mainEntity.setName("sathish");
		mainEntity.setProspectId(executionEntity.getProspectId());
		return mainEntity;
	}

	public ProfExecutionEntity convertRequestToProfHeader(String activityName, ProfUserInfoEntity entity) {
		ProfExecutionEntity executionEntity=new ProfExecutionEntity();
		executionEntity.setActionBy(entity.getUserName());
		executionEntity.setActivityName(activityName);
		executionEntity.setEntryDate(LocalDateTime.now().toString());
		String uniqueId=generateUniqueId();
		executionEntity.setProspectId("DMS_"+uniqueId);
		executionEntity.setStatus("IN PROGRESS");
		return executionEntity;
	}
	
	
}
