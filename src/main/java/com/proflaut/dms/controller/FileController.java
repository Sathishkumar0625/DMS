package com.proflaut.dms.controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

	@Value("${create.folderlocation}")
	private String folderLocation;

	@Autowired
	FileManagementServiceImpl fileManagementServiceImpl;
	private static final Logger logger = LogManager.getLogger(FileController.class);

	@Autowired
	ProfDocUploadRepository uploadRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@PostMapping("/upload")
	@Transactional
	public ResponseEntity<FileResponse> fileUpload(@RequestHeader(value = "token") String token,
			@Valid @RequestBody FileRequest fileRequest, BindingResult bindingResult) {
		logger.info("Getting into Upload");
		FileResponse fileResponse = new FileResponse();
		if (bindingResult.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder("Validation error(s): ");
			bindingResult.getFieldErrors()
					.forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
			fileResponse.setErrorMessage(errorMessage.toString());
			return new ResponseEntity<>(fileResponse, HttpStatus.BAD_REQUEST);
		}
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			fileResponse = fileManagementServiceImpl.storeFile(fileRequest, token, status, transactionManager);
			if (fileResponse != null && (!fileResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
				logger.info("Upload Success");
				ProfDocEntity docEntity = uploadRepository.findByDocNameAndFolderId(fileRequest.getDockName(),
						Integer.valueOf(fileRequest.getFolderId()));
				String path=folderLocation+File.separator+docEntity.getDocName();
				System.out.println(path);
				ProfMetaDataResponse metaDataResponse = fileManagementServiceImpl
						.save(fileRequest.getCreateTableRequests().get(0), docEntity.getId(), fileRequest);
				fileResponse.setId(docEntity.getId());
				if (metaDataResponse != null && metaDataResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
					logger.error("INSERT META DATA SUCCESS");
					return new ResponseEntity<>(fileResponse, HttpStatus.OK);
				} else {
					logger.error("Failed to create meta table");
					transactionManager.rollback(status);
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				logger.warn("Upload Failure");
				transactionManager.rollback(status);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Unexpected error during file upload", e);
			transactionManager.rollback(status);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/download")
	public ResponseEntity<FileRetreiveResponse> getDocumentById(@RequestHeader(value = "token") String token,
			@RequestParam String prospectId) {

		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(prospectId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		logger.info("Getting into Download");
		FileRetreiveResponse fileRetreiveResponse = new FileRetreiveResponse();
		try {
			fileRetreiveResponse = fileManagementServiceImpl.retreiveFile(token, prospectId);
			List<DocumentDetails> document = fileRetreiveResponse.getDocument();

			if (!document.isEmpty() && document.get(0).getDocName() != null
					&& DMSConstant.SUCCESS.equalsIgnoreCase(fileRetreiveResponse.getStatus())) {
				logger.info("Download List Success");
				return new ResponseEntity<>(fileRetreiveResponse, HttpStatus.OK);
			} else {
				fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
				logger.warn("Download List Failure");
				return new ResponseEntity<>(fileRetreiveResponse, HttpStatus.NOT_FOUND);
			}

		} catch (Exception e) {
			fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
			logger.error("Unexpected error during file retrieval", e);
			return new ResponseEntity<>(fileRetreiveResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/downloadBy")
	public ResponseEntity<Map<String, Object>> getDocumentByName(@RequestParam int id) {
		if (StringUtils.isEmpty(id)) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			Map<String, Object> fileRetreiveByResponse = null;
			logger.info("getting in to Download By");
			fileRetreiveByResponse = fileManagementServiceImpl.reteriveFileById(id);
			if (!fileRetreiveByResponse.isEmpty()) {
				logger.info(" Download BY Success");
				return new ResponseEntity<>(fileRetreiveByResponse, HttpStatus.OK);

			} else {
				logger.info(" Download BY is Failure");
				return new ResponseEntity<>(fileRetreiveByResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/createTable")
	public ResponseEntity<ProfMetaDataResponse> createTable(@RequestHeader("token") String token,
			@RequestBody CreateTableRequest createTableRequest) {
		if (StringUtils.isEmpty(token) || StringUtils.isEmpty(createTableRequest.getTableName())
				|| StringUtils.isEmpty(createTableRequest.getFileExtension())) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			metaDataResponse = fileManagementServiceImpl.createTableFromFieldDefinitions(createTableRequest, token);
			if (!metaDataResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(metaDataResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(metaDataResponse, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			metaDataResponse.setStatus(DMSConstant.FAILURE);
			metaDataResponse.setErrorMessage(e.getMessage());
			return new ResponseEntity<>(metaDataResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/share")
	public ResponseEntity<ProfEmailShareResponse> uploadFile(@RequestBody ProfEmailShareRequest emailShareRequest) {
		if (StringUtils.isEmpty(emailShareRequest.getTo()) || StringUtils.isEmpty(emailShareRequest.getDocName())) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfEmailShareResponse emailShareResponse = null;

		try {
			emailShareResponse = fileManagementServiceImpl.emailReader(emailShareRequest);
			if (!emailShareResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(emailShareResponse, HttpStatus.OK);
			} else {
				emailShareResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(emailShareResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getAllTables")
	public ResponseEntity<GetAllTableResponse> getAllMetaTables(@RequestParam String tableName) {
		GetAllTableResponse getAllTableResponse = null;
		try {
			getAllTableResponse = fileManagementServiceImpl.getAll(tableName);
			if (getAllTableResponse.getCreatedBy() != null) {
				return new ResponseEntity<>(getAllTableResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllMetaEntity")
	public ResponseEntity<List<ProfOverallMetaDataResponse>> getAllMetaData() {
		try {
			List<ProfOverallMetaDataResponse> dataResponse = fileManagementServiceImpl.getAllData();
			if (!dataResponse.isEmpty()) {
				return new ResponseEntity<>(dataResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
