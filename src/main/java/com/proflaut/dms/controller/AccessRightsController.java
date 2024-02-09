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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightResponse;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.service.impl.AccessRightsServiceImpl;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;

@RestController
@RequestMapping("/access")
@CrossOrigin
public class AccessRightsController {

	@Autowired
	FileManagementServiceImpl fileManagementServiceImpl;

	@Autowired
	AccessRightsServiceImpl accessRightsServiceImpl;

	private static final Logger logger = LogManager.getLogger(AccessRightsController.class);

	@GetMapping("/findAllFromTable")
	public ResponseEntity<Map<String, Object>> findAllFromTable(@RequestParam String tableName) {
		if (StringUtils.isEmpty(tableName)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			Map<String, Object> responseMap = accessRightsServiceImpl.findAllRowsAndColumns(tableName);
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

	@PostMapping("/saveAccess")
	public ResponseEntity<ProfAccessRightResponse> createAccess(
			@RequestBody ProfAccessRightRequest accessRightRequest) {
		if (StringUtils.isEmpty(accessRightRequest.getView()) || StringUtils.isEmpty(accessRightRequest.getWrite())
				|| StringUtils.isEmpty(accessRightRequest.getCreatedBy())) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfAccessRightResponse accessRightResponse = null;
		try {
			accessRightResponse = accessRightsServiceImpl.create(accessRightRequest);
			if (!accessRightResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getAllAccess")
	public ResponseEntity<List<ProfOverallAccessRightsResponse>> getAllAccess() {
		List<ProfOverallAccessRightsResponse> accessRightsResponses = null;
		try {
			accessRightsResponses = accessRightsServiceImpl.findAccess();
			if (!accessRightsResponses.isEmpty()) {
				return new ResponseEntity<>(accessRightsResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAccessById")
	public ResponseEntity<ProfOverallAccessRightsResponse> getAccess(@RequestParam int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfOverallAccessRightsResponse accessRightsResponses = null;
		try {
			accessRightsResponses = accessRightsServiceImpl.findAccessById(id);
			if (accessRightsResponses != null) {
				return new ResponseEntity<>(accessRightsResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
