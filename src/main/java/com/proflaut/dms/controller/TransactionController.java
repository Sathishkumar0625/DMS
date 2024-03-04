package com.proflaut.dms.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.InvoiceRequest;
import com.proflaut.dms.model.InvoiceResponse;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityResponse;
import com.proflaut.dms.model.ProfActivityReterive;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.ProfUpdateDmsMainResponse;
import com.proflaut.dms.service.impl.TransactionServiceImpl;
import com.proflaut.dms.util.AppConfiguration;

@RestController
@RequestMapping("/transaction")
@CrossOrigin
public class TransactionController {

	private final AppConfiguration appConfiguration;

	public TransactionController( AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
	}

	@Autowired
	TransactionServiceImpl transactionImpl;

	private static final Logger logger = LogManager.getLogger(TransactionController.class);

	@SuppressWarnings("unchecked")
	@PostMapping("/loadActivity")
	public ResponseEntity<ProfActivityResponse> save1() throws ParseException {
		ProfActivityResponse activityResponse = null;
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:/Users/BILLPC01/Desktop/jsonStructure.json")) {

			Object obj = jsonParser.parse(reader);
			JSONArray actLst = (JSONArray) obj;
			actLst.forEach(activity -> {

				JSONObject act = (JSONObject) activity;

				ProfActivityRequest req = new ProfActivityRequest();
				req.setKey(act.get("key").toString());
				req.setTitle(act.get("title").toString());
				req.setProcessId(act.get("processId").toString());
				req.setUserID(Integer.parseInt(act.get("userID").toString()));
				req.setGroupId(act.get("groupId").toString());
				transactionImpl.saveActivity(req);
			});

			return new ResponseEntity<>(activityResponse, HttpStatus.OK);

		} catch (IOException e) {

			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/save")
	public ResponseEntity<ProfActivityResponse> save() {
		ProfActivityResponse activityResponse = null;
		try {
			String filePath = appConfiguration.getJsonFilePath();
			File file = new File(filePath);
			ObjectMapper objectMapper = new ObjectMapper();
			ProfActivityRequest activityRequest = objectMapper.readValue(file, ProfActivityRequest.class);
			activityResponse = transactionImpl.saveActivity(activityRequest);

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
		if (StringUtils.isEmpty(userId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			List<ProfActivityReterive> activitiesList = transactionImpl.retrieveActivitiesByUserId(userId);
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
			activityResponse = transactionImpl.saveMaker(mainRequest);
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
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(userid)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			List<ProfDmsMainReterive> dmsMainReterives = transactionImpl.retrieveByMainUserId(key, userid);
			if (!dmsMainReterives.isEmpty()) {
				List<Map<String, Object>> headers = transactionImpl.retrieveHeadersByKey(key);

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
			@RequestBody ProfUpdateDmsMainRequest dmsMainRequest, @RequestParam String prospectId) {
		if (StringUtils.isEmpty(prospectId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfUpdateDmsMainResponse dmsMainResponse = null;
		try {
			dmsMainResponse = transactionImpl.updateDmsMain(dmsMainRequest, prospectId);
			if (dmsMainResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				logger.info("Updated SuccessFully");
				return new ResponseEntity<>(dmsMainResponse, HttpStatus.OK);
			} else {
				logger.info("Updation Failed");
				return new ResponseEntity<>(dmsMainResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Error At Backend");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("/saveHeader")
	private ResponseEntity<ProfUpdateDmsMainResponse> saveHeader() {
		ProfUpdateDmsMainResponse dmsMainResponsedmsMainResponse = null;

		try {
			String filePath = appConfiguration.getJsonHeaderFilePath();
			String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
			dmsMainResponsedmsMainResponse = transactionImpl.saveJsonData(jsonData);
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
	public ResponseEntity<ProfExecutionResponse> saveExcution(@RequestParam int userId,
			@RequestParam String activityName) {
		if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(activityName)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		ProfExecutionResponse executionResponse = null;
		try {
			executionResponse = transactionImpl.saveData(userId, activityName);
			if (executionResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
				return new ResponseEntity<>(executionResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(executionResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getExecutions")
	public ResponseEntity<Map<String, Object>> getExecution(@RequestParam String key, @RequestParam int userId) {
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(userId)) {
			logger.info(DMSConstant.INVALID_INPUT);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
			Map<String, Object> result = transactionImpl.getExecutionResults(key, userId);

			if (result != null && !result.isEmpty()) {
				return new ResponseEntity<>(result, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/generate")
	public ResponseEntity<InvoiceResponse> invoiceGenerator(@RequestBody InvoiceRequest invoiceRequest) {
		InvoiceResponse invoiceResponse = null;
		try {
			invoiceResponse = transactionImpl.invoice(invoiceRequest);
			if (invoiceResponse.getStatus() != null) {
				return new ResponseEntity<>(invoiceResponse, HttpStatus.OK);
			} else {
				invoiceResponse.setStatus(DMSConstant.FAILURE);
				return new ResponseEntity<>(invoiceResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
