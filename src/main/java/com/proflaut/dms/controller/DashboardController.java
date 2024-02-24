package com.proflaut.dms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.helper.ProfUserUploadDetailsResponse;
import com.proflaut.dms.service.impl.DashboardServiceImpl;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin
public class DashboardController {

	@Autowired
	DashboardServiceImpl dashboardServiceImpl;

	/*
	 * @GetMapping("/getUserUploadedDetails") public
	 * ResponseEntity<ProfUserUploadDetailsResponse>
	 * userUploadedDetails(@Param("userId") int userId) {
	 * ProfUserUploadDetailsResponse detailsResponse = null; try { detailsResponse =
	 * dashboardServiceImpl.getUploadedDetails(userId); if (detailsResponse != null)
	 * { return new ResponseEntity<>(detailsResponse, HttpStatus.OK); } else {
	 * return new ResponseEntity<>(HttpStatus.NOT_FOUND); } } catch (Exception e) {
	 * e.printStackTrace(); return new
	 * ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); }
	 * 
	 * }
	 */

	@GetMapping("/getUserUploadedDetails/{userId}")
	public ResponseEntity<ProfUserUploadDetailsResponse> getUploadedDetails(@PathVariable int userId) {
		ProfUserUploadDetailsResponse detailsResponse = null;
		try {
			detailsResponse = dashboardServiceImpl.getUploadedDetails(userId);
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
