package com.proflaut.dms.controller;

import java.util.List;
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
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.ProfMountFolderMappingRequest;
import com.proflaut.dms.model.ProfMountPointOverallResponse;
import com.proflaut.dms.model.ProfMountPointRequest;
import com.proflaut.dms.model.ProfMountPointResponse;
import com.proflaut.dms.service.impl.AccessServiceImpl;
import com.proflaut.dms.service.impl.MountPointServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/mount")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class MountPointController {

	private static final Logger logger = LogManager.getLogger(MountPointController.class);

	MountPointServiceImpl mountPointServiceImpl;

	@PostMapping("/saveMountPoint")
	public ResponseEntity<ProfMountPointResponse> saveMount(@RequestHeader("token") String token,
			@RequestBody ProfMountPointRequest mountPointRequest) {
		if (StringUtils.isEmpty(mountPointRequest.getPath())) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateMountStatus/{id}")
	public ResponseEntity<ProfMountPointResponse> updateMountStatus(@RequestHeader("token") String token,
			@PathVariable int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		ProfMountPointResponse mountPointResponse = null;
		try {
			mountPointResponse = mountPointServiceImpl.modifyMountStatus(id);
			if (!mountPointResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(mountPointResponse, HttpStatus.OK);
			} else {
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(mountPointResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getMountPointById/{id}")
	public ResponseEntity<ProfMountPointOverallResponse> getMountPoint(@PathVariable int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/saveMountMapping")
	public ResponseEntity<ProfMountPointResponse> saveFolderMapping(@RequestHeader("token") String token,
			@RequestBody ProfMountFolderMappingRequest folderMappingRequest) {
		if (StringUtils.isEmpty(folderMappingRequest.getMountPointId())) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllNotAllocateFolders/{id}")
	public ResponseEntity<List<FolderPathResponse>> getAllNotAllocateFolders(@PathVariable int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		List<FolderPathResponse> folderPathResponses = null;
		try {
			folderPathResponses = mountPointServiceImpl.getAllNotAllocate(id);
			if (!folderPathResponses.isEmpty()) {
				logger.info("GET ALL NOT ALLOCATE FOLDERS SUCCESS");
				return new ResponseEntity<>(folderPathResponses, HttpStatus.OK);
			} else {
				logger.info("GET ALL NOT ALLOCATE FOLDERS FAILURE");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/getAllAllocateFolders/{id}")
	public ResponseEntity<List<FolderPathResponse>> getAllAllocateFolders(@PathVariable int id) {
		if (StringUtils.isEmpty(id)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		List<FolderPathResponse> folderPathResponses = null;
		try {
			folderPathResponses = mountPointServiceImpl.getAllAllocate(id);
			if (!folderPathResponses.isEmpty()) {
				logger.info("GET ALL ALLOCATE FOLDERS SUCCESS");
				return new ResponseEntity<>(folderPathResponses, HttpStatus.OK);
			} else {
				logger.info("GET ALL ALLOCATE FOLDERS FAILURE");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@DeleteMapping("/unAllocateFolders")
	public ResponseEntity<ProfMountPointResponse> unAllocate(@RequestParam("folderId") int folderId,
			@RequestParam("mountId") int mountId) {
		if (StringUtils.isEmpty(folderId) || StringUtils.isEmpty(mountId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfMountPointResponse mountPointResponse = null;
		try {
			mountPointResponse = mountPointServiceImpl.unAllocateFolders(folderId, mountId);
			if (!mountPointResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				logger.info("UNALLOCATE FOLDERS SUCCESS");
				return new ResponseEntity<>(mountPointResponse, HttpStatus.OK);
			} else {
				logger.info("UNALLOCATE FOLDERS FAILURE");
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(mountPointResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
