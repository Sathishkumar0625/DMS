package com.proflaut.dms.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
import com.proflaut.dms.model.BasePdf;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.MailInfoRequest;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfMailConfigRepository;
import com.proflaut.dms.repository.ProfOldImageRepository;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;
import com.proflaut.dms.util.Compression;

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

	@Autowired
	ProfMailConfigRepository configRepository;

	public FileResponse storeFile(FileRequest fileRequest, String token) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			ProfUserPropertiesEntity userProp = fileHelper.callProfUserConnection(token);
			ProfUserInfoEntity profUserInfoEntity = profUserInfoRepository.findByUserId(userProp.getUserId());

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
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			String tableName = fileHelper.createTable(createTableRequest.getFields(), createTableRequest);
			ProfMetaDataEntity dataEntity = fileHelper.convertTableReqToMetaEntity(createTableRequest, tableName);
			entityManager.persist(dataEntity);
			metaDataResponse.setStatus(DMSConstant.SUCCESS);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaDataResponse;
	}

	@Transactional
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
					// Set EMAIL_RES_ID in ProfDocEntity
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
				emailShareResponse.setStatus(DMSConstant.FAILURE);
			}

		} catch (Exception e) {
			e.printStackTrace();
			emailShareResponse.setStatus(DMSConstant.FAILURE);
		}

		return emailShareResponse;
	}

	public String convertPdfBase64ToWordBase64(BasePdf basePdf) {
		 try {
		         // Decode PDF Base64
		        byte[] pdfBytes = Base64.getDecoder().decode(basePdf.getPdf());
		        PDDocument document = PDDocument.load(pdfBytes);
		        PDFTextStripper stripper = new PDFTextStripper();
		        String pdfText = stripper.getText(document);

		        // Create Word document
		        @SuppressWarnings("resource")
				XWPFDocument wordDocument = new XWPFDocument();
		        XWPFParagraph paragraph = wordDocument.createParagraph();

		        // Apply styles or adjust formatting as needed
		        // Example: Set font size
		        XWPFRun run = paragraph.createRun();
		        run.setFontSize(12);
		        run.setText(pdfText);

		        // Convert Word document to Base64
		        ByteArrayOutputStream out = new ByteArrayOutputStream();
		        wordDocument.write(out);
		        byte[] wordBytes = out.toByteArray();
		        String wordBase64 = Base64.getEncoder().encodeToString(wordBytes);

		        // Close resources
		        document.close();

		        return wordBase64;
		    } catch (Exception e) {
		        e.printStackTrace();
		        return null;
		    }
	}
}
