package com.proflaut.preloadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proflaut.dms.model.ProfActivityList;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfActivityReterive;
import com.proflaut.dms.service.impl.UserRegisterServiceImpl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadData {

	@Autowired
	private static UserRegisterServiceImpl userRegisterServiceImpl;

	public static void main(String[] args) {

		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader("C:/Users/BILLPC01/Desktop/jsonStructure.json")) {
			try {
				Object obj = jsonParser.parse(reader);
				JSONArray actLst = (JSONArray) obj;
				actLst.forEach(activity -> {
					JSONObject act = (JSONObject) activity;
					System.out.println(act.get("key"));
					ProfActivityRequest req = new ProfActivityRequest();
					req.setKey(act.get("key").toString());
					req.setTitle(act.get("title").toString());
					req.setProcessId(act.get("processId").toString());
					req.setUserID(Integer.parseInt(act.get("userID").toString()));
					req.setGroupId(act.get("groupId").toString());
					userRegisterServiceImpl.saveActivity(req);
				});
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Autowired
//	private static UserRegisterServiceImpl userRegisterServiceImpl;

//	public static void main(String[] args) {
//
//		try {
//			
////			Object obj = parser.parse(new FileReader("C:/Users/BILLPC01/Desktop/jsonStructure.json"));
//			
////			ProfActivityResponse activityResponse = null;
//			// TODO Auto-generated method stub
//			String filePath = "C:/Users/BILLPC01/Desktop/jsonStructure.json";
//			File file = new File(filePath);
//			ObjectMapper objectMapper = new ObjectMapper();
//			ProfActivityList activityRequestLst = objectMapper.readValue(file, ProfActivityList.class);
//			for (ProfActivityReterive req : activityRequestLst.getActivityList()) {
//				System.out.println(req.getKey());
//
//			}
////			activityResponse = 
////			userRegisterServiceImpl.saveActivity(activityRequest);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
