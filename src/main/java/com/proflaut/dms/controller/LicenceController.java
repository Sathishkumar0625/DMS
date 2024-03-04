package com.proflaut.dms.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.service.impl.LicenceServiceImpl;

@RestController
@RequestMapping("/licence")
public class LicenceController {

	private static final Logger logger = LogManager.getLogger(LicenceController.class);
	
	LicenceServiceImpl licenceServiceImpl;
	
	
	@Autowired
	public LicenceController(LicenceServiceImpl licenceServiceImpl) {
		this.licenceServiceImpl = licenceServiceImpl;
	}

	@PostMapping("/createLicence")
	public ResponseEntity<ProfLicenceResponse> create() {
		ProfLicenceResponse licenceResponse = new ProfLicenceResponse();
		try {
			licenceResponse = licenceServiceImpl.createLicence();
			if (!licenceResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(licenceResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(licenceResponse, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getLicenceKey")
	public ResponseEntity<List<ProfLicenceResponse>> getLicence() {
		List<ProfLicenceResponse> licenceResponse = new ArrayList<>();
		try {
			licenceResponse = licenceServiceImpl.getLicenceKey();
			if (!licenceResponse.isEmpty()) {
				return new ResponseEntity<>(licenceResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(licenceResponse, HttpStatus.NOT_ACCEPTABLE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
