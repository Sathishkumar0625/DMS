package com.proflaut.dms.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.CreateTableRequest;
import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.model.ProfMetaDataResponse;
import com.proflaut.dms.model.ProfOverallMetaDataResponse;
import com.proflaut.dms.service.impl.MetaServiceImpl;

@RestController
@RequestMapping("/meta")
@CrossOrigin
public class MetaController {
	
	private static final Logger logger = LogManager.getLogger(MetaController.class);
	
	@Autowired
	MetaServiceImpl metaServiceImpl;
	
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
			metaDataResponse = metaServiceImpl.createTableFromFieldDefinitions(createTableRequest, token);
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
	
	@GetMapping("/getAllTables")
	public ResponseEntity<GetAllTableResponse> getAllMetaTables(@RequestParam int id) {
		GetAllTableResponse getAllTableResponse = null;
		try {
			getAllTableResponse = metaServiceImpl.getAll(id);
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
			List<ProfOverallMetaDataResponse> dataResponse = metaServiceImpl.getAllData();
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
	
	@GetMapping("/getMetaById")
	public ResponseEntity<ProfOverallMetaDataResponse> getMetaDataById(@RequestParam int id) {
		ProfOverallMetaDataResponse dataResponse=null;
		try {
			 dataResponse = metaServiceImpl.getMetaData(id);
			if (dataResponse != null) {
				return new ResponseEntity<>(dataResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/findAllFromTable")
	public ResponseEntity<Map<String, Object>> findAllFromTable(@RequestParam String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			Map<String, Object> responseMap = metaServiceImpl.findAllRowsAndColumns(tableName);
			if (!responseMap.isEmpty()) {
				return new ResponseEntity<>(responseMap, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
