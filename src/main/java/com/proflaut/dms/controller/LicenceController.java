package com.proflaut.dms.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.service.impl.LicenceServiceImpl;

@RestController
@RequestMapping("/licence")
@CrossOrigin
public class LicenceController {


	@Autowired
	LicenceServiceImpl licenceServiceImpl;

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
			e.printStackTrace();
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
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}