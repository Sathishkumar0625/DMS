package com.proflaut.preloadData;

import java.io.FileReader;
import java.io.IOException;

import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.service.impl.UserRegisterServiceImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadData {

	@Autowired
	private static UserRegisterServiceImpl userRegisterServiceImpl;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ParseException {

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

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
