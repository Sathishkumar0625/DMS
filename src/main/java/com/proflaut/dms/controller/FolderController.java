package com.proflaut.dms.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.helper.FolderHelper;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderRetreiveResponse;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.model.ProfFolderRetrieveResponse;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;
import com.proflaut.dms.service.impl.FolderServiceImpl;

@RestController
@RequestMapping("/folder")
@CrossOrigin
public class FolderController {

	FolderServiceImpl folderServiceImpl;

	FileManagementServiceImpl serviceIMPL;

	FolderHelper folderHelper;
	
	@Autowired
	public FolderController(FolderServiceImpl folderServiceImpl, FileManagementServiceImpl serviceIMPL,
			FolderHelper folderHelper) {
		this.folderServiceImpl = folderServiceImpl;
		this.serviceIMPL = serviceIMPL;
		this.folderHelper = folderHelper;
	}

	private static final Logger logger = LogManager.getLogger(FolderController.class);

	@PostMapping("/create")
	public ResponseEntity<FileResponse> createFolder(@RequestHeader("token") String token,
			@Valid @RequestBody FolderFO folderFO) {

		if (StringUtils.isEmpty(folderFO.getFolderName()) || StringUtils.isEmpty(folderFO.getMetaDataId())
				|| StringUtils.isEmpty(token) || StringUtils.isEmpty(folderFO.getMountId())) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		FileResponse fileResponse = new FileResponse();
		try {
			fileResponse = folderServiceImpl.saveFolder(folderFO, token);
			if (DMSConstant.SUCCESS.equals(fileResponse.getStatus())) {
				return new ResponseEntity<>(fileResponse, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(fileResponse, HttpStatus.NOT_ACCEPTABLE);
			}

		} catch (Exception e) {

			fileResponse.setStatus(DMSConstant.FAILURE);
			fileResponse.setProspectId(folderFO.getProspectId());
			if (e.getMessage().contains(DMSConstant.CONSTRAINTVIOLATIONEXCEPTION)) {
				fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
			} else {
				fileResponse.setErrorMessage(e.getMessage());

			}
			return new ResponseEntity<>(fileResponse, HttpStatus.NOT_ACCEPTABLE);

		}
	}

	@GetMapping("/ById/{id}")
	public ResponseEntity<FileResponse> getById(@PathVariable("id") Integer id, FileRequest fileRequest) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		FileResponse fileResponse = null;
		try {
			fileResponse = folderServiceImpl.retriveFile(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fileResponse != null && fileResponse.getProspectId() != null) {

			return new ResponseEntity<>(fileResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/getById/{id}")
	public ResponseEntity<Folders> findById(@PathVariable("id") Integer id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Folders folderRetreiveResponse = null;
		try {
			folderRetreiveResponse = folderServiceImpl.retreive(id);
			if (folderRetreiveResponse.getFolderName() != null) {
				return new ResponseEntity<>(folderRetreiveResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getAllParentFolders")
	public ResponseEntity<FolderRetreiveResponse> getAll(@RequestHeader("token") String token) {
		if (StringUtils.isEmpty(token)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			FolderRetreiveResponse folderRetreiveResponse = folderServiceImpl.getAllFolders(token);
			if (folderRetreiveResponse != null
					&& folderRetreiveResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(folderRetreiveResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("getByParentId")
	public ResponseEntity<ProfFolderRetrieveResponse> getFoldersByParentId(@RequestParam int parentFolderID) {
		if (StringUtils.isEmpty(parentFolderID)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfFolderRetrieveResponse folderRetrieveResponse = null;
		try {
			folderRetrieveResponse = folderServiceImpl.fetchByParentId(parentFolderID);
			if (!folderRetrieveResponse.getSubFolderPath().isEmpty()) {
				return new ResponseEntity<>(folderRetrieveResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
