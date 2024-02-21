package com.proflaut.dms.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfMountFolderMappingRequest;
import com.proflaut.dms.model.ProfMountPointOverallResponse;
import com.proflaut.dms.model.ProfMountPointRequest;
import com.proflaut.dms.model.ProfMountPointResponse;
import com.proflaut.dms.service.impl.MountPointServiceImpl;

@RestController
@RequestMapping("/mount")
public class MountPointController {

	private static final Logger logger = LogManager.getLogger(MountPointController.class);

	@Autowired
	MountPointServiceImpl mountPointServiceImpl;

	@PostMapping("/saveMountPoint")
	public ResponseEntity<ProfMountPointResponse> saveMount(@RequestHeader("token") String token,
			@RequestBody ProfMountPointRequest mountPointRequest) {
		ProfMountPointResponse mountPointResponse = null;
		try {
			mountPointResponse = mountPointServiceImpl.saveMountPoint(mountPointRequest, token);
			if (!mountPointResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				logger.info("SAVE MOUNT POINT SUCCESS");
				return new ResponseEntity<>(mountPointResponse, HttpStatus.OK);
			} else {
				logger.info("SAVE MOUNT POINT FAILURE");
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(mountPointResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllMountPoint")
	public ResponseEntity<List<ProfMountPointOverallResponse>> getAllMontPoint() {
		List<ProfMountPointOverallResponse> overallResponses = null;
		try {
			overallResponses = mountPointServiceImpl.mountPoint();
			if (!overallResponses.isEmpty()) {
				logger.info("GET ALL MOUNT POINT FAILURE");
				return new ResponseEntity<>(overallResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getMountPointById/{id}")
	public ResponseEntity<ProfMountPointOverallResponse> getMountPoint(@PathVariable int id) {
		ProfMountPointOverallResponse overallResponses = null;
		try {
			overallResponses = mountPointServiceImpl.findById(id);
			if (overallResponses != null) {
				logger.info("GET MOUNT POINT SUCCESS");
				return new ResponseEntity<>(overallResponses, HttpStatus.OK);
			} else {
				logger.info("GET MOUNT POINT FAILURE -> {}", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/saveMountMapping")
	public ResponseEntity<ProfMountPointResponse> saveFolderMapping(@RequestHeader("token") String token,
			@RequestBody ProfMountFolderMappingRequest folderMappingRequest) {
		ProfMountPointResponse mountPointResponse = null;
		try {
			mountPointResponse = mountPointServiceImpl.saveMountPointMapping(folderMappingRequest, token);
			if (!mountPointResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				logger.info("SAVE MOUNT MAPPING SUCCESS");
				return new ResponseEntity<>(mountPointResponse, HttpStatus.OK);
			} else {
				logger.info("SAVE MOUNT MAPPING POINT FAILURE");
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(mountPointResponse, HttpStatus.NOT_FOUND);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
