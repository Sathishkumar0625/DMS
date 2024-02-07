package com.proflaut.dms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.model.GetAllTableResponse;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;
import com.proflaut.dms.service.impl.MetaServiceImpl;

@RestController
@RequestMapping("/meta")
@CrossOrigin
public class MetaController {

	@Autowired
	FileManagementServiceImpl fileManagementServiceImpl;

	@Autowired
	MetaServiceImpl metaServiceImpl;

	@GetMapping("/findAllFromTable")
	public ResponseEntity<Map<String, Object>> findAllFromTable(@RequestParam String tableName) {
		try {
			Map<String, Object> responseMap = metaServiceImpl.findAllRowsAndColumns(tableName);
			if (!responseMap.isEmpty()) {
				return new ResponseEntity<>(responseMap, HttpStatus.OK);
			}else {
			return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
