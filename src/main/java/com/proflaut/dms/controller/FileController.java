package com.proflaut.dms.controller;

import java.util.List;
import org.springframework.util.StringUtils;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.BasePdf;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

	@Autowired
	FileManagementServiceImpl fileManagementServiceImpl;
	private static final Logger logger = LogManager.getLogger(FileController.class);

	@PostMapping("/upload")
	public ResponseEntity<FileResponse> fileUpload(@RequestHeader(value = "token") String token,
			@Valid @RequestBody FileRequest fileRequest, BindingResult bindingResult) {
		logger.info("getting in to Upload");
		FileResponse fileResponse = new FileResponse();
		if (bindingResult.hasErrors() && bindingResult.getFieldError() != null) {
			fileResponse.setErrorMessage("Validation error: " + bindingResult.getFieldError().getDefaultMessage());
			return new ResponseEntity<>(fileResponse, HttpStatus.BAD_REQUEST);
		}

		try {
			fileResponse = fileManagementServiceImpl.storeFile(fileRequest, token);
			if (fileResponse != null && (!fileResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
				logger.info("getting in to Upload Success");
				return new ResponseEntity<>(fileResponse, HttpStatus.OK);
			} else {
				logger.warn("Upload Failure");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Unexpected error during file upload", e);
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
	public ResponseEntity<FileRetreiveByResponse> getDocumentByName(@RequestParam int id) {
		if (StringUtils.isEmpty(id)) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		try {
			logger.info("getting in to Download By");
			FileRetreiveByResponse fileRetreiveByResponse = fileManagementServiceImpl.reteriveFileByNameAndId(id);
			if (fileRetreiveByResponse.getImage() != null) {
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
	public ResponseEntity<ProfMetaDataResponse> createTable(@RequestBody CreateTableRequest createTableRequest) {
		ProfMetaDataResponse metaDataResponse = new ProfMetaDataResponse();
		try {
			metaDataResponse = fileManagementServiceImpl.createTableFromFieldDefinitions(createTableRequest);
			return new ResponseEntity<>(metaDataResponse, HttpStatus.OK);
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

	@PostMapping("/convert")
	public ResponseEntity<String> convertPdfToWord(@RequestBody BasePdf pdfBase) {
		String wordBase64 = fileManagementServiceImpl.convertPdfBase64ToWordBase64(pdfBase);
		if (wordBase64 != null) {
			return ResponseEntity.ok(wordBase64);
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error converting PDF to Word.");
		}
	}
}
