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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfGroupInfoResponse;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfSignupUserRequest;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.service.impl.GroupServiceImpl;

@RestController
@RequestMapping("/group")
@CrossOrigin
public class GroupController {

	@Autowired
	GroupServiceImpl groupServiceImpl;
	private static final Logger logger = LogManager.getLogger(GroupController.class);

	@PostMapping("/create")
	public ResponseEntity<ProfGroupInfoResponse> create(@Valid @RequestBody ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			logger.info("GEtting into Create Group");
			groupInfoResponse = groupServiceImpl.createGroup(groupInfoRequest);
			return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			groupInfoResponse.setErrorMessage(DMSConstant.GROUPNAME_ALREADY_EXIST);
			groupInfoResponse.setStatus(DMSConstant.FAILURE);
			return new ResponseEntity<>(groupInfoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ProfGroupInfoResponse> hideGroup(@PathVariable String id,
			@RequestBody ProfGroupInfoRequest groupInfoRequest) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			int groupId = Integer.parseInt(id);
			groupInfoResponse = groupServiceImpl.updateStatus(groupId, groupInfoRequest);
			if (groupInfoResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllGroup")
	public ResponseEntity<List<ProfOverallGroupInfoResponse>> getAllGroupInfo() {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.find();
			if (!groupInfoResponses.isEmpty()) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/createUser")
	public ResponseEntity<ProfGroupInfoResponse> createUserGroup(
			@Valid @RequestBody ProfUserGroupMappingRequest mappingRequest) {
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			groupInfoResponse = groupServiceImpl.createGroup(mappingRequest);
			if (groupInfoResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllUsers")
	public ResponseEntity<List<ProfOveralUserInfoResponse>> getAllUser() {
		List<ProfOveralUserInfoResponse> userInfoResponses = null;
		try {
			userInfoResponses = groupServiceImpl.findUsers();
			if (!userInfoResponses.isEmpty()) {
				return new ResponseEntity<>(userInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateUsers/{userId}")
	public ResponseEntity<ProfGroupInfoResponse> updateSignup(@RequestBody ProfSignupUserRequest userRequest,
			@PathVariable int userId) {
		if (StringUtils.isEmpty(userId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfGroupInfoResponse infoResponse = null;
		try {
			infoResponse = groupServiceImpl.updateSignupUser(userRequest, userId);
			if (!infoResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(infoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(infoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}
}
