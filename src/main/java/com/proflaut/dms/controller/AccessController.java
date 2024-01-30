package com.proflaut.dms.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.service.impl.AccessServiceImpl;

@RestController
@RequestMapping("/access")
@CrossOrigin
public class AccessController {
	
	
	@Autowired
	AccessServiceImpl accessServiceImpl;
	
	@PostMapping("/signup")
	public ResponseEntity<UserRegResponse> createUser(@Valid @RequestBody UserInfo userInfo,
			BindingResult bindingResult) {
		UserRegResponse userRegResponse = new UserRegResponse();
		if (bindingResult.hasErrors()) {
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			StringBuilder errorMessage = new StringBuilder(DMSConstant.VALIDATION_FAILED);
			for (FieldError fieldError : fieldErrors) {
				errorMessage.append(fieldError.getDefaultMessage()).append("; ");
			}
			userRegResponse.setStatus(DMSConstant.FAILURE);
			userRegResponse.setErrorMessage(errorMessage.toString());
			userRegResponse.setUserId(0);
			userRegResponse.setEmail(userInfo.getEmail());
			userRegResponse.setUserName(userInfo.getUserName());
			return new ResponseEntity<>(userRegResponse, HttpStatus.BAD_REQUEST);
		}
		try {
			userRegResponse = accessServiceImpl.saveUser(userInfo);
			return new ResponseEntity<>(userRegResponse, HttpStatus.CREATED);
		} catch (Exception e) {
			userRegResponse.setStatus(DMSConstant.FAILURE);
			userRegResponse.setErrorMessage(DMSConstant.ERRORMESSAGE);
			userRegResponse.setUserId(0);
			userRegResponse.setEmail(userInfo.getEmail());
			userRegResponse.setUserName(userInfo.getUserName());
			return new ResponseEntity<>(userRegResponse, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserInfo userInfo) {
		LoginResponse loginResponse = null;
		try {
			loginResponse = accessServiceImpl.getUser(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (loginResponse != null && (!loginResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(loginResponse, HttpStatus.NOT_FOUND);
		}
	}
}