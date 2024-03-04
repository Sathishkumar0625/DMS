package com.proflaut.dms.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightResponse;
import com.proflaut.dms.model.ProfAccessRightsUpdateRequest;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.service.impl.AccessRightsServiceImpl;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;

@RestController
@RequestMapping("/access")
@CrossOrigin
public class AccessRightsController {

	FileManagementServiceImpl fileManagementServiceImpl;

	AccessRightsServiceImpl accessRightsServiceImpl;

	@Autowired
	public AccessRightsController(FileManagementServiceImpl fileManagementServiceImpl,
			AccessRightsServiceImpl accessRightsServiceImpl) {
		this.fileManagementServiceImpl = fileManagementServiceImpl;
		this.accessRightsServiceImpl = accessRightsServiceImpl;
	}

	private static final Logger logger = LogManager.getLogger(AccessRightsController.class);

	@PostMapping("/saveAccess")
	public ResponseEntity<ProfAccessRightResponse> createAccess(
			@Valid @RequestBody ProfAccessRightRequest accessRightRequest) {
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAccessById")
	public ResponseEntity<ProfOverallAccessRightsResponse> getAccess(@RequestParam String id) {
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
				return new ResponseEntity<>(accessRightsResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateAccess/{id}")
	public ResponseEntity<ProfAccessRightResponse> updateSignup(
			@RequestBody ProfAccessRightsUpdateRequest accessRightUpdateRequest, @PathVariable int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfAccessRightResponse accessRightResponse = null;
		try {
			accessRightResponse = accessRightsServiceImpl.updateAccessRights(accessRightUpdateRequest, id);
			if (!accessRightResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping("/deleteUserAccess")
	public ResponseEntity<ProfAccessRightResponse> deleteAccess(@RequestParam("userId") int userId,
			@RequestParam("accessId") int accessId) {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(accessId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfAccessRightResponse accessRightResponse = null;
		try {
			accessRightResponse = accessRightsServiceImpl.deleteUserAccess(userId, accessId);
			if (!accessRightResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.OK);
			} else {
				accessRightResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(accessRightResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/deleteGroupAccess")
	public ResponseEntity<ProfAccessRightResponse> deleteAccessgroup(@RequestParam("groupId") int groupId,
			@RequestParam("accessId") int accessId) {
		if (StringUtils.isEmpty(groupId) || StringUtils.isEmpty(accessId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfAccessRightResponse accessRightResponse = null;
		try {
			accessRightResponse = accessRightsServiceImpl.deleteGroupAccess(groupId, accessId);
			if (!accessRightResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(accessRightResponse, HttpStatus.OK);
			} else {
				accessRightResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(accessRightResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllNotAccessUsers/{accessId}")
	public ResponseEntity<List<ProfOveralUserInfoResponse>> getAllNotAccessUsers(@PathVariable int accessId) {
		List<ProfOveralUserInfoResponse> overalUserInfoResponses = null;
		try {
			overalUserInfoResponses = accessRightsServiceImpl.findAllNotAccessUsers(accessId);
			if (!overalUserInfoResponses.isEmpty()) {
				return new ResponseEntity<>(overalUserInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllNotAccessGroups/{accessId}")
	public ResponseEntity<List<ProfOverallGroupInfoResponse>> getAllNotAccessGroups(@PathVariable int accessId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = null;
		try {
			groupInfoResponses = accessRightsServiceImpl.getAllNotAccessGroups(accessId);
			if (!groupInfoResponses.isEmpty()) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
