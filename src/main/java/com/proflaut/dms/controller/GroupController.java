package com.proflaut.dms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.proflaut.dms.model.ProfUserGroupMappingRequest;
import com.proflaut.dms.service.impl.GroupServiceImpl;

@RestController
@RequestMapping("/group")
@CrossOrigin
public class GroupController {

	@Autowired
	GroupServiceImpl groupServiceImpl;

	@PostMapping("/create")
	public ResponseEntity<ProfGroupInfoResponse> create(@RequestBody ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoResponse groupInfoResponse = null;
		try {
			groupInfoResponse = groupServiceImpl.createGroup(groupInfoRequest);
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

	@PutMapping("/update/{id}")
	public ResponseEntity<String> hideGroup(@PathVariable String id,
			@RequestBody ProfGroupInfoRequest groupInfoRequest) {
		String groupInfoResponse = null;
		try {
			groupInfoResponse = groupServiceImpl.updateStatus(Integer.parseInt(id), groupInfoRequest);
			if (groupInfoResponse.equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(groupInfoResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAll")
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
			@RequestBody ProfUserGroupMappingRequest mappingRequest) {
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
}
