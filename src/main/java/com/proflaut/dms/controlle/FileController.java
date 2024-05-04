package com.proflaut.dms.controlle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.ImageRequest;
import com.proflaut.dms.model.ImageResponse;
import com.proflaut.dms.model.ProfEmailShareRequest;
import com.proflaut.dms.model.ProfEmailShareResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallCountResponse;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;
import com.proflaut.dms.service.impl.MetaServiceImpl;

@RestController
@RequestMapping("/file")
public class FileController {

	@Value("${create.folderlocation}")
	private String folderLocation;

	FileManagementServiceImpl fileManagementServiceImpl;
	private static final Logger logger = LogManager.getLogger(FileController.class);

	ProfDocUploadRepository uploadRepository;

	private PlatformTransactionManager transactionManager;

	MetaServiceImpl metaServiceImpl;

	@Autowired
	public FileController(FileManagementServiceImpl fileManagementServiceImpl, ProfDocUploadRepository uploadRepository,
			MetaServiceImpl metaServiceImpl, PlatformTransactionManager transactionManager) {
		this.fileManagementServiceImpl = fileManagementServiceImpl;
		this.uploadRepository = uploadRepository;
		this.metaServiceImpl = metaServiceImpl;
		this.transactionManager = transactionManager;
	}

	@PostMapping("/upload")
	@Transactional
	public ResponseEntity<FileResponse> fileUpload(@RequestHeader(value = "token") String token,
			@Valid @RequestBody FileRequest fileRequest, BindingResult bindingResult) throws IOException {
		long startTime = System.nanoTime();

		logger.info("Getting into Upload");
		FileResponse fileResponse = new FileResponse();
		if (bindingResult.hasErrors()) {
			StringBuilder errorMessage = new StringBuilder("Validation error(s): ");
			bindingResult.getFieldErrors()
					.forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
			fileResponse.setErrorMessage(errorMessage.toString());
			return new ResponseEntity<>(fileResponse, HttpStatus.BAD_REQUEST);
		}
		Path paths = null;
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			fileResponse = fileManagementServiceImpl.storeFile(fileRequest, token, status, transactionManager);
			if (fileResponse != null && (!fileResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
				logger.info("Upload Success");
				ProfDocEntity docEntity = uploadRepository.findByDocNameAndFolderId(fileRequest.getDockName(),
						Integer.valueOf(fileRequest.getFolderId()));
				String path = folderLocation + File.separator + docEntity.getDocPath();
				paths = Paths.get(path);
				ProfMetaDataResponse metaDataResponse = metaServiceImpl.save(
						fileRequest.getCreateTableRequests().get(0), docEntity.getId(), fileRequest, paths, docEntity);
				fileResponse.setId(docEntity.getId());
				if (metaDataResponse != null && metaDataResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
					logger.info("INSERT META DATA SUCCESS");
					long endTime = System.nanoTime();
					long executionTime = (endTime - startTime) / 1000000;
					docEntity.setUploadExecutionTime((int) executionTime);
					uploadRepository.save(docEntity);
					logger.info("Counting to 10000000 takes --> {} ", executionTime);
					return new ResponseEntity<>(fileResponse, HttpStatus.OK);
				} else {
					logger.error("Failed to create meta table");
					transactionManager.rollback(status);
					return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				logger.warn("Upload Failure");
				metaServiceImpl.delete(paths);
				transactionManager.rollback(status);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("Unexpected error during file upload", e);
			metaServiceImpl.delete(paths);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getCount")
	public ResponseEntity<ProfOverallCountResponse> getCount() {
		ProfOverallCountResponse countResponse = null;
		try {
			countResponse = fileManagementServiceImpl.reteriveCount();
			if (countResponse != null) {
				logger.info(" Download BY Success");
				return new ResponseEntity<>(countResponse, HttpStatus.OK);

			} else {
				logger.info(" Download BY is Failure");
				return new ResponseEntity<>(countResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/sharpenedImage")
	public ResponseEntity<ImageResponse> getImage(@RequestBody ImageRequest imageRequest) {
		if (StringUtils.isEmpty(imageRequest.getImage())) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ImageResponse imageResponse = null;
		try {
			imageResponse = fileManagementServiceImpl.getImage(imageRequest);
			if (imageResponse != null) {
				return new ResponseEntity<>(imageResponse, HttpStatus.OK);

			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/downloadHistory/{docId}")
	public ResponseEntity<ProfEmailShareResponse> downloadHistory(@RequestHeader("token") String token,
			@PathVariable int docId) {
		long startTime = System.nanoTime();
		if (StringUtils.isEmpty(docId) || StringUtils.isEmpty(token)) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfEmailShareResponse emailShareResponse = null;

		try {
			emailShareResponse = fileManagementServiceImpl.downloadHist(docId, token, startTime);
			if (!emailShareResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(emailShareResponse, HttpStatus.OK);
			} else {
				emailShareResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(emailShareResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/compression")
	public ResponseEntity<ImageResponse> compression() {
		ImageResponse imageResponse = null;
		try {
			imageResponse = fileManagementServiceImpl.converToImage();
			if (imageResponse != null) {
				return new ResponseEntity<>(imageResponse, HttpStatus.OK);

			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
