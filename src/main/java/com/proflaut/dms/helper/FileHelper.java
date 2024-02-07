package com.proflaut.dms.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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
import com.proflaut.dms.entity.ProfMailConfigEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfOldImageEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.BulkEmailSender;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FieldDefinitionResponse;
import com.proflaut.dms.model.FieldDefnition;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;
import com.proflaut.dms.util.Compression;
import javax.activation.DataHandler;
import javax.activation.DataSource;

@Component
public class FileHelper {

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfDocUploadRepository docUploadRepository;

	@Autowired
	ProfOldImageRepository imageRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Value("${upload.filelocation}")
	private String fileLocation;

	@Value("${create.folderlocation}")
	private String folderLocation;

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger logger = LogManager.getLogger(FileHelper.class);

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	private int tableCount = 1;

	@Transactional
	public boolean storeDocument(FileRequest fileRequest, int uId, String uName, String token) throws CustomException {
		boolean isFileCreated = false;
		try {
			FolderEntity entity = folderRepository.findById(Integer.parseInt(fileRequest.getFolderId()));

			if (entity != null) {
				int count = entity.getParentFolderID();
				count += 1;
				String path = entity.getFolderPath();
				UUID uuid = UUID.randomUUID();
				String fileName = uuid.toString();

				ProfDocEntity existingDocEntity = docUploadRepository
						.findByDocNameAndFolderId(fileRequest.getDockName(),Integer.parseInt(fileRequest.getFolderId()));

				if (existingDocEntity != null) {
					File newFile = new File(path + File.separator + fileName);

					try (FileWriter fileWriter = new FileWriter(newFile)) {
						String compressedBytes = Compression.compressAndReturnB64(fileRequest.getImage());
						PasswordEncDecrypt td = new PasswordEncDecrypt();
						String encrypted = td.encrypt(compressedBytes);

						fileWriter.write(encrypted);
						isFileCreated = true;
					} catch (IOException e) {
						handleIOException(e);
					}

					ProfOldImageEntity imageEntity = convertFileReqToOldImage(existingDocEntity, uId, uName,
							fileRequest);
					imageRepository.save(imageEntity);
					moveDocumentToBackup(existingDocEntity, fileRequest, entity);

					existingDocEntity.setDocPath(fileName);
					docUploadRepository.save(existingDocEntity);
				} else {
					File file = new File(path + File.separator + fileName);
					//folderRepository.updateParentFolderIdAndFolderPath(count, entity.getProspectId());

					try (FileWriter fileWriter = new FileWriter(file)) {
						String compressedBytes = Compression.compressAndReturnB64(fileRequest.getImage());
						PasswordEncDecrypt td = new PasswordEncDecrypt();
						String encrypted = td.encrypt(compressedBytes);
						fileWriter.write(encrypted);
						isFileCreated = true;
					} catch (IOException e) {
						handleIOException(e);
					}
					ProfDocEntity profDocEnt = convertFileRequesttoProfDoc(fileRequest, token, entity, fileName);
					docUploadRepository.save(profDocEnt);

				}
			}
		} catch (Exception e) {
			handleGenericException(e);
		}
		return isFileCreated;
	}

	public ProfDocEntity convertFileRequesttoProfDoc(FileRequest fileRequest, String token, FolderEntity entity,
			String fileName) {
		ProfDocEntity ent = new ProfDocEntity();
		ProfUserPropertiesEntity userProp = profUserPropertiesRepository.findByToken(token);
		if (userProp != null) {
			ent.setCreatedBy(userProp.getUserId());
		}
		ent.setFolderId(entity.getId());
		ent.setUploadTime(formatCurrentDateTime());
		ent.setProspectId(entity.getFolderName());
		ent.setDocName(fileRequest.getDockName());
		ent.setDocPath(fileName);
		ent.setExtention(fileRequest.getExtention());
		ent.setIsEmail("N");
		return ent;
	}

	private ProfOldImageEntity convertFileReqToOldImage(ProfDocEntity existingDocEntity, int uId, String uName,
			FileRequest fileRequest) {
		ProfOldImageEntity oldImageEntity = new ProfOldImageEntity();
		oldImageEntity.setDocName(existingDocEntity.getDocName());
		oldImageEntity.setDocPath(existingDocEntity.getDocPath());
		oldImageEntity.setUserName(uName);
		//oldImageEntity.setProspectId(fileRequest.getProspectId());
		oldImageEntity.setCreatedBy(String.valueOf(uId));
		oldImageEntity.setExtention(existingDocEntity.getExtention());
		return oldImageEntity;
	}

	private void moveDocumentToBackup(ProfDocEntity existingDocEntity, FileRequest fileRequest, FolderEntity entity)
			throws CustomException {
		String backupFolderPath = folderLocation + "Backup_" + entity.getFolderName();
		Path existingPath = Paths.get(entity.getFolderPath(), existingDocEntity.getDocPath());
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

	public String retrieveDocument(ProfDocEntity docEntity, FolderEntity entity) {
		String decompressedBytes = "";
		String decrypted = "";
		String path = entity.getFolderPath() + File.separator + docEntity.getDocPath();

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
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decompressedBytes;
	}

	public ProfUserPropertiesEntity callProfUserConnection(String token) {
		return profUserPropertiesRepository.findByToken(token);

	}

	public String createTable(List<FieldDefnition> fieldDefinitions, CreateTableRequest createTableRequest) {
		StringBuilder queryBuilder = new StringBuilder();
		String tableName = createTableRequest.getTableName() + "_" + tableCount;
		queryBuilder.append("CREATE TABLE ").append(tableName).append(" (");
		queryBuilder.append("ID SERIAL PRIMARY KEY, ");
		queryBuilder.append("DOC_ID INTEGER, ");
		for (Iterator<FieldDefnition> it = fieldDefinitions.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			String fieldName = field.getFieldName();
			String fieldType = field.getFieldType();
			String mandatory = field.getMandatory();
			int maxLength = Integer.parseInt(field.getMaxLength());

			queryBuilder.append(fieldName).append(" ").append(getDatabaseType(fieldType, maxLength));
			if ("Y".equalsIgnoreCase(mandatory)) {
				queryBuilder.append(" NOT NULL");
			}
			if (it.hasNext()) {
				queryBuilder.append(", ");
			}
		}
		queryBuilder.append(")");
		entityManager.createNativeQuery(queryBuilder.toString()).executeUpdate();
		tableCount++;
		return tableName;
	}

	private String getDatabaseType(String fieldType, int maxLength) {
		if ("String".equalsIgnoreCase(fieldType)) {
			return "VARCHAR(" + maxLength + ")";
		} else if ("Integer".equalsIgnoreCase(fieldType)) {
			return "INT";
		} else {
			return fieldType;
		}
	}

	public ProfMetaDataEntity convertTableReqToMetaEntity(CreateTableRequest createTableRequest, String tableName,
			ProfUserInfoEntity entity) {
		ProfMetaDataEntity metaDataEntity = new ProfMetaDataEntity();
		metaDataEntity.setTableName(tableName);
		metaDataEntity.setFileExtension(createTableRequest.getFileExtension());
		metaDataEntity.setCreatedBy(entity.getUserName());
		metaDataEntity.setCreatedAt(formatCurrentDateTime());
		metaDataEntity.setName(createTableRequest.getTableName());
		metaDataEntity.setStatus("A");
		return metaDataEntity;
	}

	private void handleIOException(IOException e) throws CustomException {
		throw new CustomException("IOException: " + e.getMessage());
	}

	private void handleGenericException(Exception e) throws CustomException {
		throw new CustomException("An unexpected error occurred: " + e.getMessage());
	}

	public boolean sendMail(MailInfoRequest mailInfoRequest, byte[] fileBytes, String extension,
			ProfDocEntity docEntity) throws MessagingException {
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

	public GetAllTableResponse convertEntityToResponse(ProfMetaDataEntity dataEntity, EntityManager entityManager) {
		GetAllTableResponse allTableResponse = new GetAllTableResponse();
		allTableResponse.setId(dataEntity.getId());
		allTableResponse.setCreatedAt(dataEntity.getCreatedAt());
		allTableResponse.setCreatedBy(dataEntity.getCreatedBy());
		allTableResponse.setFileExtention(dataEntity.getFileExtension());
		String tableName = dataEntity.getTableName().toLowerCase();
		if (tableName != null) {
			List<FieldDefinitionResponse> definitionResponses = getColumnDetails(tableName, entityManager);
			allTableResponse.setFieldNames(definitionResponses);
		}
		return allTableResponse;
	}

	private List<FieldDefinitionResponse> getColumnDetails(String tableName, EntityManager entityManager) {
		List<FieldDefinitionResponse> definitionResponses = new ArrayList<>();

		try {
			String sqlQuery = "SELECT column_name, data_type, is_nullable, character_maximum_length "
					+ "FROM information_schema.columns " + "WHERE table_name = :tableName";

			@SuppressWarnings("unchecked")
			List<Object[]> result = entityManager.createNativeQuery(sqlQuery).setParameter("tableName", tableName)
					.getResultList();

			for (Object[] row : result) {
				String columnName = (String) row[0];
				String dataType = (String) row[1];
				String isNullable = (String) row[2];
				Integer characterMaxLength = (Integer) row[3];

				FieldDefinitionResponse fieldDefinitionResponse = new FieldDefinitionResponse();
				fieldDefinitionResponse.setFieldName(columnName);
				 List<String> values = fetchDataFromTable(columnName, tableName);
		            fieldDefinitionResponse.setValue(String.join(",", values));
				fieldDefinitionResponse
						.setFieldType("character varying".equalsIgnoreCase(dataType) ? "String" : "Integer");
				fieldDefinitionResponse.setMandatory("NO".equalsIgnoreCase(isNullable) ? "Y" : "N");
				fieldDefinitionResponse.setMaxLength(characterMaxLength != null ? characterMaxLength.toString() : null);
				definitionResponses.add(fieldDefinitionResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return definitionResponses;
	}

	private List<String> fetchDataFromTable(String columnName, String tableName) {
	    List<String> values = new ArrayList<>();
	    try {
	        String sqlQuery = "SELECT " + columnName + " FROM " + tableName;
	        @SuppressWarnings("unchecked")
			List<Object> results = entityManager.createNativeQuery(sqlQuery).getResultList();
	        for (Object result : results) {
	            if (result != null) {
	                values.add(result.toString());
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return values;
	}

	@Transactional
	public ProfMetaDataResponse insertDataIntoTable(String tableName, List<FieldDefnition> fields, Integer id) {
		ProfMetaDataResponse dataResponse = new ProfMetaDataResponse();
		StringBuilder insertQueryBuilder = new StringBuilder();
		insertQueryBuilder.append("INSERT INTO ").append(tableName).append(" (");
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition field = it.next();
			insertQueryBuilder.append(field.getFieldName());
			if (it.hasNext()) {
				insertQueryBuilder.append(", ");
			}
		}
		insertQueryBuilder.append(", doc_id");
		insertQueryBuilder.append(") VALUES (");
		for (Iterator<FieldDefnition> it = fields.iterator(); it.hasNext();) {
			FieldDefnition fieldValue = it.next();
			insertQueryBuilder.append(getFormattedValue(fieldValue));
			if (it.hasNext()) {
				insertQueryBuilder.append(", ");
			}
		}
		insertQueryBuilder.append(", ").append(id);

		insertQueryBuilder.append(")");

		try {
			entityManager.createNativeQuery(insertQueryBuilder.toString()).executeUpdate();
			dataResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			dataResponse.setStatus(DMSConstant.FAILURE);
		}
		return dataResponse;
	}

	private String getFormattedValue(FieldDefnition fieldValue) {
		if ("String".equalsIgnoreCase(fieldValue.getFieldType())) {
			return "'" + fieldValue.getValue() + "'";
		} else {
			return fieldValue.getValue();
		}
	}

	public ProfOverallMetaDataResponse convertToResponse(ProfMetaDataEntity metaDataEntity) {
		ProfOverallMetaDataResponse metaDataResponse = new ProfOverallMetaDataResponse();
		metaDataResponse.setId(metaDataEntity.getId());
		metaDataResponse.setCreatedAt(metaDataEntity.getCreatedAt());
		metaDataResponse.setFileExtenion(metaDataEntity.getFileExtension());
		metaDataResponse.setName(metaDataEntity.getName());
		metaDataResponse.setStatus(metaDataEntity.getStatus());
		metaDataResponse.setTableName(metaDataEntity.getTableName());
		metaDataResponse.setCreatedBy(metaDataEntity.getCreatedBy());
		return metaDataResponse;
	}

}
