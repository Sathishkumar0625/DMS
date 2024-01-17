package com.proflaut.dms.controller;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityResponse;
import com.proflaut.dms.service.impl.UserRegisterServiceImpl;

@RestController
@RequestMapping("/preLoad")
public class PreLoadController {

	private UserRegisterServiceImpl userRegisterServiceImpl;

	@Autowired
	public PreLoadController(UserRegisterServiceImpl userRegisterServiceImpl) {
		this.userRegisterServiceImpl = userRegisterServiceImpl;
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/loadActivity")
	public ResponseEntity<ProfActivityResponse> save() throws ParseException {
		ProfActivityResponse activityResponse=null;
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
					userRegisterServiceImpl.saveActivity(req);
				});

				return new ResponseEntity<>(activityResponse, HttpStatus.OK);
			
		} catch (IOException e) {

			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} 

	}
	
	

}
