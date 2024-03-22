package com.proflaut.dms.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.ProfJobPackRequest;
import com.proflaut.dms.service.impl.JobPackServiceImpl;

@RestController
@RequestMapping("/jobPack")
public class JobPackController {

	private static final Logger logger = LogManager.getLogger(JobPackController.class);

	JobPackServiceImpl jobPackServiceImpl;

	@Autowired
	public JobPackController(JobPackServiceImpl jobPackServiceImpl) {
		this.jobPackServiceImpl = jobPackServiceImpl;
	}
	
	@PostMapping("/getDocFields")
	public ResponseEntity<String> getDocFields(@RequestBody ProfJobPackRequest jobPackRequest)  {
		String licenceResponse = null;
		try {
			licenceResponse = jobPackServiceImpl.getFields(jobPackRequest);
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

	@GetMapping("/getAllTemplate")
	public ResponseEntity<List<Map<String, Object>>> getAllTemplates() {
		List<Map<String, Object>> response = null;
		try {
			response = jobPackServiceImpl.getAllTemp();
			if (!response.isEmpty()) {
				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
