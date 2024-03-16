package com.proflaut.dms.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import java.util.Base64;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.crypto.NoSuchPaddingException;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfDownloadHistoryEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.entity.ProfMailConfigEntity;
import com.proflaut.dms.entity.ProfMountPointEntity;
import com.proflaut.dms.entity.ProfMountPointFolderMappingEntity;
import com.proflaut.dms.entity.ProfOldImageEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.BulkEmailSender;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.GroupUserList;
import com.proflaut.dms.model.Groups;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfMountPointRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;
import com.proflaut.dms.util.Compression;

@Component
@Transactional
public class FileHelper {

	ProfUserInfoRepository infoRepository;

	FolderRepository folderRepository;

	ProfDocUploadRepository docUploadRepository;

	ProfOldImageRepository imageRepository;

	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Value("${upload.filelocation}")
	private String fileLocation;

	@Value("${create.folderlocation}")
	private String folderLocation;

	@PersistenceContext
	private EntityManager entityManager;

	ProfMountPointRepository mountPointRepository;

	ProfUserGroupMappingRepository groupMappingRepository;

	@Autowired
	public FileHelper(ProfUserInfoRepository infoRepository, FolderRepository folderRepository,
			ProfDocUploadRepository docUploadRepository, ProfOldImageRepository imageRepository,
			ProfUserPropertiesRepository profUserPropertiesRepository, ProfMountPointRepository mountPointRepository,
			ProfUserGroupMappingRepository groupMappingRepository) {
		this.infoRepository = infoRepository;
		this.folderRepository = folderRepository;
		this.docUploadRepository = docUploadRepository;
		this.imageRepository = imageRepository;
		this.profUserPropertiesRepository = profUserPropertiesRepository;
		this.mountPointRepository = mountPointRepository;
		this.groupMappingRepository = groupMappingRepository;
	}

	private static final Logger logger = LogManager.getLogger(FileHelper.class);

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public boolean storeDocument(FileRequest fileRequest, int uId, String uName, String token,
			ProfMountPointFolderMappingEntity folderMappingEntity) throws CustomException {
		boolean isFileCreated = false;
		String fileName = null;
		try {
			ProfMountPointEntity mountPointEntity = mountPointRepository
					.findById(folderMappingEntity.getMountPointId());
			FolderEntity entity = folderRepository.findById(Integer.parseInt(fileRequest.getFolderId()));

			if (entity != null && mountPointEntity.getPath() != null) {
				String path = mountPointEntity.getPath();
				UUID uuid = UUID.randomUUID();
				fileName = uuid.toString();

				ProfDocEntity existingDocEntity = docUploadRepository.findByDocNameAndFolderId(
						fileRequest.getDockName(), Integer.parseInt(fileRequest.getFolderId()));

				if (existingDocEntity != null) {
					isFileCreated = createFileAndSaveData(path, fileName, fileRequest.getImage());
					String fileSize = getBase64ImageSizeInKB(fileRequest.getImage());
					ProfOldImageEntity imageEntity = convertFileReqToOldImage(existingDocEntity, uId, uName, fileSize);
					imageRepository.save(imageEntity);
					moveDocumentToBackup(existingDocEntity, entity);
					existingDocEntity.setDocPath(fileName);
					existingDocEntity.setFileSize(fileSize);
					docUploadRepository.save(existingDocEntity);
				} else {
					isFileCreated = createFileAndSaveData(path, fileName, fileRequest.getImage());
					String fileSize = getBase64ImageSizeInKB(fileRequest.getImage());
					ProfDocEntity profDocEnt = convertFileRequesttoProfDoc(fileRequest, token, entity, fileName,
							fileSize);
					docUploadRepository.save(profDocEnt);
				}
			}
		} catch (Exception e) {
			rollbackAndDeleteFile(fileName);
			handleGenericException(e);
		} finally {
			if (!isFileCreated) {
				rollbackAndDeleteFile(fileName);
			}
		}
		return isFileCreated;
	}

	private boolean createFileAndSaveData(String path, String fileName, String imageData)
			throws IOException, CustomException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		try (FileWriter fileWriter = new FileWriter(new File(path + File.separator + fileName))) {
			String compressedBytes = Compression.compressAndReturnB64(imageData);
			PasswordEncDecrypt td = new PasswordEncDecrypt();
			String encrypted = td.encrypt(compressedBytes);
			fileWriter.write(encrypted);
			return true;
		} catch (IOException e) {
			handleIOException(e);
			return false;
		}
	}

	public static String getBase64ImageSizeInKB(String base64Image) {
		// Decode Base64 string into byte array
		byte[] imageData = Base64.getDecoder().decode(base64Image);

		// Calculate size of byte array
		long sizeInBytes = imageData.length;

		// Convert size to kilobytes
		return "" + sizeInBytes / 1024 + "kb";
	}

	private void rollbackAndDeleteFile(String fileName) {
		String partialFilePath = folderLocation + File.separator + fileName;
		Path path = Paths.get(partialFilePath);
		File partialFile = new File(partialFilePath);
		if (partialFile.exists()) {
			try {
				Files.delete(path);
				logger.info("Partially uploaded file deleted successfully.");
			} catch (IOException e) {
				logger.error("Failed to delete partially uploaded file.", e);
			}
		} else {
			logger.info("Partially uploaded file does not exist.");
		}
	}

	public ProfDocEntity convertFileRequesttoProfDoc(FileRequest fileRequest, String token, FolderEntity entity,
			String fileName, String fileSize) {
		ProfDocEntity ent = new ProfDocEntity();
		ProfUserPropertiesEntity userProp = profUserPropertiesRepository.findByToken(token);
		if (userProp != null) {
			ent.setCreatedBy(userProp.getUserName());
		}
		ent.setFileSize(fileSize);
		ent.setFolderId(entity.getId());
		ent.setUploadTime(formatCurrentDateTime());
		ent.setProspectId(entity.getFolderName());
		ent.setDocName(fileRequest.getDockName());
		ent.setDocPath(fileName);
		ent.setExtention(fileRequest.getExtention());
		ent.setIsEmail("N");
		ent.setMetaId(Integer.valueOf(fileRequest.getCreateTableRequests().get(0).getMetadataId()));
		return ent;
	}

	private ProfOldImageEntity convertFileReqToOldImage(ProfDocEntity existingDocEntity, int uId, String uName,
			String fileSize) {
		ProfOldImageEntity oldImageEntity = new ProfOldImageEntity();
		oldImageEntity.setDocName(existingDocEntity.getDocName());
		oldImageEntity.setDocPath(existingDocEntity.getDocPath());
		oldImageEntity.setUserName(uName);
		oldImageEntity.setDocId(String.valueOf(existingDocEntity.getId()));
		oldImageEntity.setCreatedBy(String.valueOf(uId));
		oldImageEntity.setExtention(existingDocEntity.getExtention());
		oldImageEntity.setFolderId(existingDocEntity.getFolderId());
		oldImageEntity.setMetaId(existingDocEntity.getMetaId());
		oldImageEntity.setFileSize(fileSize);
		return oldImageEntity;
	}

	private void moveDocumentToBackup(ProfDocEntity existingDocEntity, FolderEntity entity) throws CustomException {
		String backupFolderPath = folderLocation;
		Path existingPath = Paths.get(entity.getIsParent(), existingDocEntity.getDocPath());
		Path backupFolder = Paths.get(backupFolderPath);

		if (!Files.exists(backupFolder)) {
			try {
				Files.createDirectories(backupFolder);
			} catch (IOException e) {
				handleIOException(e);
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
			handleIOException(e);
		}

	}

	public String retrievDocument(List<ProfDocEntity> profDocEntity, String decrypted,
			FileRetreiveResponse fileRetreiveResponse, ProfUserInfoEntity infoEntity) {
		List<DocumentDetails> document = new ArrayList<>();

		for (int i = 0; i < profDocEntity.size(); i++) {
			folderRepository.findByProspectId(profDocEntity.get(i).getProspectId());

			if (!org.springframework.util.StringUtils.isEmpty(profDocEntity.get(i).getDocPath())) {
				try {
					String filePath = folderLocation + File.separator + profDocEntity.get(i).getDocPath();
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
					logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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

	public String retrieveDocument(ProfDocEntity docEntity, ProfMountPointEntity mountPointEntity) {
		String decompressedBytes = "";
		String decrypted = "";
		String path = mountPointEntity.getPath() + File.separator + docEntity.getDocPath();

		try {
			logger.info("File path -> {} ", path);

			if (!Files.exists(Paths.get(path))) {
				logger.info("File does not exist");
				return decrypted;
			}

			String content = new String(Files.readAllBytes(Paths.get(path)));
			PasswordEncDecrypt td = new PasswordEncDecrypt();
			decrypted = td.decrypt(content);

			decompressedBytes = Compression.decompressB64(decrypted);
		} catch (AccessDeniedException e) {
			logger.info("Access denied -> {} ", e.getMessage());
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return decompressedBytes;
	}

	public ProfUserPropertiesEntity callProfUserConnection(String token) {
		return profUserPropertiesRepository.findByToken(token);
	}

	private void handleIOException(IOException e) throws CustomException {
		throw new CustomException("IOException: " + e.getMessage());
	}

	private void handleGenericException(Exception e) throws CustomException {
		throw new CustomException("An unexpected error occurred: " + e.getMessage());
	}

	public boolean sendMail(MailInfoRequest mailInfoRequest, byte[] fileBytes, String extension,
			ProfDocEntity docEntity) throws MessagingException, CustomException {
		boolean isMail = false;
		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		BulkEmailSender bulkEmailSender = new BulkEmailSender(mailInfoRequest, props);
		MimeMessage mimeMessage = bulkEmailSender.getMimeMessage();
		Multipart multipart = new MimeMultipart();

		BodyPart textPart = new MimeBodyPart();
		textPart.setContent("Please find the attachment in the email regarding ....", "text/html;charset=utf-8");
		multipart.addBodyPart(textPart);

		if (fileBytes != null && fileBytes.length > 0) {
			BodyPart filePart = new MimeBodyPart();
			DataSource source = new ByteArrayDataSource(fileBytes, getContentType(extension));
			filePart.setDataHandler(new DataHandler(source));
			filePart.setFileName(docEntity.getDocName() + "." + extension);
			multipart.addBodyPart(filePart);
			isMail = true;
		}

		mimeMessage.setContent(multipart);

		bulkEmailSender.sendEmail(mimeMessage);
		return isMail;
	}

	private String getContentType(String extension) {
		switch (extension.toLowerCase()) {
		case "pdf":
			return "application/pdf";
		case "jpg":
		case "jpeg":
			return "image/jpeg";
		case "png":
			return "image/png";
		case "xlsx":
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		case "xls":
			return "application/vnd.ms-excel";
		case "text":
			return "text/plain";
		default:
			return "application/octet-stream";
		}
	}

	public ProfMailConfigEntity convertemailShareReqToMailConf(ProfEmailShareRequest emailShareRequest) {
		ProfMailConfigEntity configEntity = new ProfMailConfigEntity();
		configEntity.setSender(emailShareRequest.getFrom());
		configEntity.setReceiver(emailShareRequest.getTo());
		configEntity.setIsEmail("Y");
		configEntity.setSendAt(formatCurrentDateTime());
		return configEntity;
	}

	public long getTotalFileSize(List<ProfDocEntity> docEntities) {
		long totalFileSize = 0;
		for (ProfDocEntity docEntity : docEntities) {
			String fileSizeString = docEntity.getFileSize();
			String numericValue = fileSizeString.replaceAll("[^\\d.]", "");
			long fileSize = Long.parseLong(numericValue);
			totalFileSize += fileSize;
		}
		return totalFileSize;
	}

	public List<Groups> getGroupInfo(ProfGroupInfoEntity groupInfoEntity, List<Groups> groups) {
		Groups group = new Groups();
		group.setGroupName(groupInfoEntity.getGroupName());

		String userCount = groupMappingRepository.countByGroupId(String.valueOf(groupInfoEntity.getId()));
		group.setUserCount(String.valueOf(userCount));

		List<Integer> userIds = groupMappingRepository.findUserIdsByGroupId(String.valueOf(groupInfoEntity.getId()));
		if (!userIds.isEmpty()) {
			List<ProfUserInfoEntity> userIdsAndNames = infoRepository.findByUserIdIn(userIds);
			List<GroupUserList> groupUserLists = userIdsAndNames.stream().map(userId -> {
				GroupUserList groupUserList = new GroupUserList();
				groupUserList.setUserId(userId.getUserId());
				groupUserList.setUserName(userId.getUserName());
				return groupUserList;
			}).collect(Collectors.toList());

			group.setGroupUserLists(groupUserLists);
			groups.add(group);
			return groups;
		} else {
			GroupUserList groupUserList = new GroupUserList();
			groupUserList.setErrorMessage("No user Found In That Group");
			groups.add(group);
			return groups;
		}

	}

	public ProfDownloadHistoryEntity convertRequestToDownloadHistory(int docId,
			ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfDownloadHistoryEntity downloadHistoryEntity = new ProfDownloadHistoryEntity();
		downloadHistoryEntity.setDocId(docId);
		downloadHistoryEntity.setDownloadedDate(formatCurrentDateTime());
		downloadHistoryEntity.setUserId(profUserPropertiesEntity.getUserId());
		downloadHistoryEntity.setUserName(profUserPropertiesEntity.getUserName());
		return downloadHistoryEntity;
	}

}
