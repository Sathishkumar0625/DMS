package com.proflaut.dms.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.helper.ProfUserUploadDetailsResponse;
import com.proflaut.dms.model.ImageRequest;
import com.proflaut.dms.model.ImageResponse;
import com.proflaut.dms.model.ProfUserGroupDetailsResponse;
import com.proflaut.dms.service.impl.DashboardServiceImpl;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

	DashboardServiceImpl dashboardServiceImpl;
		
	@Autowired
	public DashboardController(DashboardServiceImpl dashboardServiceImpl) {
		this.dashboardServiceImpl = dashboardServiceImpl;
	}

	@GetMapping("/getUserUploadedDetails")
	public ResponseEntity<ProfUserUploadDetailsResponse> getUploadedDetails(@RequestHeader("token") String token) {
		ProfUserUploadDetailsResponse detailsResponse = null;
		try {
			detailsResponse = dashboardServiceImpl.getUploadedDetails(token);
			if (detailsResponse != null) {
				return new ResponseEntity<>(detailsResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getUserGroupDetails")
	public ResponseEntity<List<ProfUserGroupDetailsResponse>> getUserGroupDetails(
			@RequestHeader("token") String token) {
		List<ProfUserGroupDetailsResponse> detailsResponse = null;
		try {
			detailsResponse = dashboardServiceImpl.getUserDetails(token);
			if (detailsResponse != null) {
				return new ResponseEntity<>(detailsResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/usersGraph")
	public ResponseEntity<List<Map<String, String>>> getUsersGraph(@RequestHeader("token") String token) {
		try {
			List<Map<String, String>> counts = dashboardServiceImpl.getUserCounts();
			return new ResponseEntity<>(counts, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@PostMapping("/ocrImage")
	public ResponseEntity<List<ImageResponse>> getOcrImage(@RequestBody ImageRequest imageRequest) {
		if (StringUtils.isEmpty(imageRequest.getImage())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		List<ImageResponse> imageResponse=null;
		try {
			imageResponse = dashboardServiceImpl.getOcrImage(imageRequest);
			if (imageResponse != null) {
				return new ResponseEntity<>(imageResponse, HttpStatus.OK);

			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getUserFullDetails")
	public ResponseEntity<ProfUserUploadDetailsResponse> getUserDetails(@RequestHeader("token") String token) {
		ProfUserUploadDetailsResponse detailsResponse = null;
		try {
			detailsResponse = dashboardServiceImpl.getUserFullDetails(token);
			if (detailsResponse != null) {
				return new ResponseEntity<>(detailsResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
