package com.proflaut.dms.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfAssignUserRequest;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfGroupInfoResponse;
import com.proflaut.dms.model.ProfOveralUserInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.model.ProfSignupUserRequest;
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.service.impl.AccessServiceImpl;
import com.proflaut.dms.service.impl.GroupServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/group")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class GroupController {

	GroupServiceImpl groupServiceImpl;

	private static final Logger logger = LogManager.getLogger(GroupController.class);

	@PostMapping("/createGroupInfo")
	public ResponseEntity<ProfGroupInfoResponse> create(@RequestHeader("token") String token,
			@Valid @RequestBody ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			logger.info("GEtting into Create Group");
			groupInfoResponse = groupServiceImpl.createGroup(groupInfoRequest, token);
			return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			groupInfoResponse.setErrorMessage(DMSConstant.GROUPNAME_ALREADY_EXIST);
			groupInfoResponse.setStatus(DMSConstant.FAILURE);
			return new ResponseEntity<>(groupInfoResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateGroupStatus/{id}")
	public ResponseEntity<ProfGroupInfoResponse> hideGroup(@PathVariable String id,
			@RequestBody ProfGroupInfoRequest groupInfoRequest) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			int groupId = Integer.parseInt(id);
			groupInfoResponse = groupServiceImpl.updateGroup(groupId, groupInfoRequest);
			if (groupInfoResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllGroup")
	public ResponseEntity<List<ProfOverallGroupInfoResponse>> getAllGroupInfo() {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.find();
			if (groupInfoResponses.get(0).getGroupName() != null) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/assignGroup")
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/assignUser")
	public ResponseEntity<ProfGroupInfoResponse> assignUser(@RequestBody ProfAssignUserRequest assignUserRequest) {
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			groupInfoResponse = groupServiceImpl.createAssignUser(assignUserRequest);
			if (groupInfoResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getGroupByUserId/{userId}")
	public ResponseEntity<List<ProfOverallGroupInfoResponse>> getGroupInfo(@PathVariable int userId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.findById(userId);
			if (!groupInfoResponses.get(0).getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getUserByGroupId/{groupId}")
	public ResponseEntity<List<ProfOveralUserInfoResponse>> getAllUserInfo(@PathVariable int groupId) {
		List<ProfOveralUserInfoResponse> userInfoResponses = null;
		try {
			userInfoResponses = groupServiceImpl.getUsersByGroupId(groupId);
			if (!userInfoResponses.isEmpty()) {
				return new ResponseEntity<>(userInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAssignGroupinfo/{userId}")
	public ResponseEntity<List<ProfOverallGroupInfoResponse>> getAssignGroupInfo(@PathVariable int userId) {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.getAssignGroupinfo(userId);
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

	@DeleteMapping("/deleteAssignGroup")
	public ResponseEntity<ProfGroupInfoResponse> deleteAssignGroup(@RequestParam int groupId,
			@RequestParam int userId) {
		ProfGroupInfoResponse groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.deleteAssGroup(groupId, userId);
			if (!groupInfoResponses.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAssignUserInfo/{groupId}")
	public ResponseEntity<List<ProfOveralUserInfoResponse>> getAssignUserInfo(@PathVariable int groupId) {
		List<ProfOveralUserInfoResponse> overalUserInfoResponses = null;
		try {
			overalUserInfoResponses = groupServiceImpl.getAssignUserinfo(groupId);
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

	@DeleteMapping("/deleteAssignUser")
	public ResponseEntity<ProfGroupInfoResponse> deleteAssignUser(@RequestParam int groupId, @RequestParam int userId) {
		ProfGroupInfoResponse groupInfoResponses = null;
		try {
			groupInfoResponses = groupServiceImpl.deleteAssUsers(groupId, userId);
			if (!groupInfoResponses.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getUser")
	public ResponseEntity<ProfOveralUserInfoResponse> getUserById(@RequestHeader("token") String token) {
		ProfOveralUserInfoResponse userInfoResponses = null;
		try {
			userInfoResponses = groupServiceImpl.findUserByToken(token);
			if (userInfoResponses != null) {
				return new ResponseEntity<>(userInfoResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
