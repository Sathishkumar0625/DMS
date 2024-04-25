package com.proflaut.dms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.BookmarkResponse;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FolderBookmarkRequest;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.service.impl.HomeServiceImpl;

@RestController
@RequestMapping("/home")
public class HomeController {

	HomeServiceImpl homeServiceImpl;

	@Autowired
	public HomeController(HomeServiceImpl homeServiceImpl) {
		this.homeServiceImpl = homeServiceImpl;
	}

	private static final Logger logger = LogManager.getLogger(HomeController.class);

	@GetMapping("/check")
	public String check() {
		return "Controller is working";
	}

	@PostMapping("/saveFolderBookmark")
	public ResponseEntity<Map<String, String>> bookmark(@RequestBody FolderBookmarkRequest bookmarkRequest,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.saveBookmark(bookmarkRequest, token);
			if (response.get("Status").equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/saveFileBookmark")
	public ResponseEntity<Map<String, String>> fileBookmark(@RequestBody FileBookMarkRequest fileBookMarkRequest,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.saveFileBookmark(fileBookMarkRequest, token);
			if (response.get("Status").equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getAllFilesAndFolders")
	public ResponseEntity<BookmarkResponse> getAllGroupInfo(@RequestHeader("token") String token) {
		BookmarkResponse bookmarkResponse = null;
		try {
			bookmarkResponse = homeServiceImpl.findAll(token);
			if (!bookmarkResponse.getFiles().isEmpty()) {
				return new ResponseEntity<>(bookmarkResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(bookmarkResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}