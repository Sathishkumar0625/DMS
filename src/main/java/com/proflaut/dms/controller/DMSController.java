package com.proflaut.dms.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.model.AccountDetailsRequest;
import com.proflaut.dms.model.AccountDetailsResponse;
import com.proflaut.dms.model.DocumentDetails;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FileRetreiveByResponse;
import com.proflaut.dms.model.FileRetreiveResponse;
import com.proflaut.dms.model.LoginResponse;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityResponse;
import com.proflaut.dms.model.ProfActivityReterive;
import com.proflaut.dms.model.ProfDmsHeaderReterive;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.ProfUpdateDmsMainResponse;
import com.proflaut.dms.model.UserInfo;
import com.proflaut.dms.model.UserRegResponse;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;
import com.proflaut.dms.service.impl.UserRegisterServiceImpl;

@RestController
@RequestMapping("/dmsCheck")
@CrossOrigin
public class DMSController {

	private final UserRegisterServiceImpl userRegisterServiceImpl;

	private static final Logger logger = LogManager.getLogger(DMSController.class);

	@Autowired
	public DMSController(UserRegisterServiceImpl userRegisterServiceImpl) {
		this.userRegisterServiceImpl = userRegisterServiceImpl;
	}
	
	

	@Autowired
	FileManagementServiceImpl fileManagementServiceImpl;

	@GetMapping("/get")
	public String test() {
		return "DMS Api is working!";
	}

	@PostMapping("/signup")
	public ResponseEntity<UserRegResponse> createUser(@RequestBody UserInfo userInfo) {
		UserRegResponse userRegResponse = new UserRegResponse();
		try {
			userRegResponse = userRegisterServiceImpl.saveUser(userInfo);
			return new ResponseEntity<>(userRegResponse, HttpStatus.CREATED);
		} catch (Exception e) {
			userRegResponse.setStatus(DMSConstant.FAILURE);
			if (e.getMessage().contains(DMSConstant.HIBERNATEEXCEPTION)) {
				userRegResponse.setErrorMessage(DMSConstant.ERRORMESSAGE);
			} else {
				userRegResponse.setErrorMessage(e.getMessage());

			}
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
			loginResponse = userRegisterServiceImpl.getUser(userInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (loginResponse != null && (!loginResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
			return new ResponseEntity<>(loginResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(loginResponse, HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<FileResponse> fileUpload(@RequestHeader(value = "token") String token,
			@RequestBody FileRequest fileRequest) {
		FileResponse fileResponse = null;
		try {
			fileResponse = fileManagementServiceImpl.storeFile(fileRequest, token);
		} catch (Exception e) {

			e.printStackTrace();
		}

		if (fileResponse != null && (!fileResponse.getStatus().equalsIgnoreCase(DMSConstant.FAILURE))) {
			return new ResponseEntity<>(fileResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/download")
	public ResponseEntity<FileRetreiveResponse> getDocumentById(@RequestHeader(value = "token") String token) {
		FileRetreiveResponse fileRetreiveResponse = fileManagementServiceImpl.retreiveFile(token);

		List<DocumentDetails> document = fileRetreiveResponse.getDocument();

		if (document.get(0).getDocName() != null
				&& fileRetreiveResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {

			return new ResponseEntity<>(fileRetreiveResponse, HttpStatus.OK);
		} else {
			fileRetreiveResponse.setStatus(DMSConstant.FAILURE);
			return new ResponseEntity<>(fileRetreiveResponse, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/getBy")
	public ResponseEntity<FileRetreiveByResponse> getDocumentByName(@RequestParam int docId,@RequestParam String docName){
		FileRetreiveByResponse fileRetreiveByResponse=null;
		try {
			fileRetreiveByResponse=fileManagementServiceImpl.reteriveFileByNameAndId(docId,docName);
			if (fileRetreiveByResponse != null) {
				return new ResponseEntity<>(fileRetreiveByResponse,HttpStatus.OK);
			}else {
				return new ResponseEntity<>(fileRetreiveByResponse,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
	}

	@PostMapping("/accountregistry")
	public ResponseEntity<AccountDetailsResponse> addAccountDetails(
			@RequestBody AccountDetailsRequest accountDetailsRequest) {
		AccountDetailsResponse accountDetailsResponse = new AccountDetailsResponse();
		try {
			accountDetailsResponse = userRegisterServiceImpl.addCustomer(accountDetailsRequest);
			return new ResponseEntity<>(accountDetailsResponse, HttpStatus.CREATED);
		} catch (Exception e) {
			accountDetailsResponse.setStatus(DMSConstant.FAILURE);
			if (e.getMessage().contains(DMSConstant.HIBERNATEEXCEPTION)) {
				accountDetailsResponse.setErrorMessage(DMSConstant.ERRORMESSAGE);
			} else {
				accountDetailsResponse.setErrorMessage(e.getMessage());

			}

			return new ResponseEntity<>(accountDetailsResponse, HttpStatus.NOT_ACCEPTABLE);
		}

	}

	@PostMapping("/save1")
	public ResponseEntity<ProfActivityResponse> save(@RequestBody ProfActivityRequest activityRequest) {
		ProfActivityResponse activityResponse = null;
		try {
			activityResponse = userRegisterServiceImpl.saveActivity(activityRequest);
			if (activityResponse.getStatus().equals(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(activityResponse, HttpStatus.OK);
			} else {
				activityResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(activityResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/save")
	public ResponseEntity<ProfActivityResponse> save() {
		ProfActivityResponse activityResponse = null;
		try {
			String filePath = "C:/Users/BILLPC01/Desktop/jsonStructure.json";
			File file = new File(filePath);
			ObjectMapper objectMapper = new ObjectMapper();
			ProfActivityRequest activityRequest = objectMapper.readValue(file, ProfActivityRequest.class);
			activityResponse = userRegisterServiceImpl.saveActivity(activityRequest);

			if (activityResponse.getStatus().equals(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(activityResponse, HttpStatus.OK);
			} else {
				activityResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(activityResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/retrieve/{userId}")
	public ResponseEntity<List<ProfActivityReterive>> retrieveActivitiesByUserId(@PathVariable int userId) {
		try {
			List<ProfActivityReterive> activitiesList = userRegisterServiceImpl.retrieveActivitiesByUserId(userId);
			if (!activitiesList.isEmpty()) {
				return new ResponseEntity<>(activitiesList, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/saveMaker")
	public ResponseEntity<ProfActivityResponse> saveMaker(@RequestBody ProfDmsMainRequest mainRequest) {
		ProfActivityResponse activityResponse = null;
		try {
			activityResponse = userRegisterServiceImpl.saveMaker(mainRequest);
			if (activityResponse.getStatus().equals(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(activityResponse, HttpStatus.OK);
			} else {
				activityResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(activityResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/findBy")
	@CrossOrigin
	public ResponseEntity<Map<String, Object>> retrieveByKey(@RequestParam String key, @RequestParam int userid) {
		try {
			List<ProfDmsMainReterive> dmsMainReterives = userRegisterServiceImpl.retrieveByMainUserId(key, userid);
			if (!dmsMainReterives.isEmpty()) {
				List<Map<String, Object>> headers = userRegisterServiceImpl.retrieveHeadersByKey(key);

				Map<String, Object> response = new HashMap<>();
				response.put("headers", headers);
				response.put(key, dmsMainReterives);

				return new ResponseEntity<>(response, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/update")
	private ResponseEntity<ProfUpdateDmsMainResponse> updateDmsMain(
			@RequestBody ProfUpdateDmsMainRequest dmsMainRequest) {
		ProfUpdateDmsMainResponse dmsMainResponse = null;
		try {
			dmsMainResponse = userRegisterServiceImpl.updateDmsMain(dmsMainRequest);
			if (dmsMainResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(dmsMainResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(dmsMainResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/save-header")
	private ResponseEntity<ProfUpdateDmsMainResponse> saveHeader() {
		ProfUpdateDmsMainResponse dmsMainResponsedmsMainResponse = null;

		try {
			String filePath = "C:/Users/BILLPC01/Desktop/jsonStructureHeader.json";
			String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
			dmsMainResponsedmsMainResponse = userRegisterServiceImpl.saveJsonData(jsonData);
			if (dmsMainResponsedmsMainResponse != null) {
				return new ResponseEntity<>(dmsMainResponsedmsMainResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(dmsMainResponsedmsMainResponse, HttpStatus.NOT_FOUND);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/saveExecution")
	public ResponseEntity<ProfExecutionResponse> saveExcution(@RequestParam int userId, @RequestParam String activityName){
		ProfExecutionResponse executionResponse=null;
		try {
			executionResponse=userRegisterServiceImpl.saveData(userId,activityName);
			if (executionResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(executionResponse,HttpStatus.OK);
			}else {
				return new ResponseEntity<>(executionResponse,HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
