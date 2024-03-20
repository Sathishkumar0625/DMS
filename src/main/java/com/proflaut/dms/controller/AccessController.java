package com.proflaut.dms.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.ProfForgotpassResponse;
import com.proflaut.dms.model.ProfUserLogoutResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.service.impl.AccessServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/access")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AccessController {

	private static final Logger logger = LogManager.getLogger(AccessController.class);

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
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(userRegResponse, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody UserInfo userInfo) {
		if (StringUtils.isEmpty(userInfo.getUserName()) && StringUtils.isEmpty(userInfo.getPassword())) {
			logger.warn(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		LoginResponse loginResponse = null;
		try {
			loginResponse = accessServiceImpl.getUser(userInfo);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}

		if (loginResponse != null && (!loginResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(loginResponse, HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/logout/{userId}")
	public ResponseEntity<ProfUserLogoutResponse> logout(@PathVariable int userId) {
		ProfUserLogoutResponse logoutResponse = null;
		try {
			logoutResponse = accessServiceImpl.deleteUser(userId);
			if (!logoutResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(logoutResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(logoutResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<ProfForgotpassResponse> forgot(@RequestParam String mailId) {
		ProfForgotpassResponse forgotpassResponse = null;
		try {
			forgotpassResponse = accessServiceImpl.forgotPassword(mailId);
			if (!forgotpassResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/verifyOtp")
	public ResponseEntity<ProfForgotpassResponse> verifyOTP(@RequestBody Map<String, String> data) {
		ProfForgotpassResponse forgotpassResponse = null;
		try {
			forgotpassResponse = accessServiceImpl.verifyOtp(data);
			if (!forgotpassResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/savePassword")
	public ResponseEntity<ProfForgotpassResponse> savePassword(@RequestBody Map<String, String> data) {
		ProfForgotpassResponse forgotpassResponse = null;
		try {
			forgotpassResponse = accessServiceImpl.savePass(data);
			if (!forgotpassResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping("/updateUserStatus/{userId}")
	public ResponseEntity<ProfForgotpassResponse> upadteStatus(@PathVariable int userId) {
		ProfForgotpassResponse forgotpassResponse = null;
		try {
			forgotpassResponse = accessServiceImpl.modifyStatus(userId);
			if (!forgotpassResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE)) {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(forgotpassResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
