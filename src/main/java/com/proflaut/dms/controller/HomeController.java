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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.model.BookmarkResponse;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.Files;
import com.proflaut.dms.model.FolderBookmarkRequest;
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.GetAllRecentFilesResponse;
import com.proflaut.dms.model.GetAllRecentFolderResponse;
import com.proflaut.dms.model.SearchFilesResponse;
import com.proflaut.dms.model.SearchFolderResponse;
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
	public ResponseEntity<BookmarkResponse> getAllFilesFolders(@RequestHeader("token") String token) {
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

	@PostMapping("/saveRecentFolders")
	public ResponseEntity<Map<String, String>> addRecentFolders(@RequestBody FolderBookmarkRequest bookmarkRequest,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.addRecentFol(bookmarkRequest, token);
			if (response.get(DMSConstant.STATUS).equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/saveRecentFiles")
	public ResponseEntity<Map<String, String>> addRecentFiles(@RequestBody FileBookMarkRequest fileBookmarkRequest,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.addRecentFil(fileBookmarkRequest, token);
			if (response.get(DMSConstant.STATUS).equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/getAllRecentFolders")
	public ResponseEntity<List<GetAllRecentFolderResponse>> getAllRecentFolders(@RequestHeader("token") String token) {
		List<GetAllRecentFolderResponse> getAllRecentFolderResponse = null;
		try {
			getAllRecentFolderResponse = homeServiceImpl.findAllRecentFolders(token);
			if (!getAllRecentFolderResponse.isEmpty()) {
				return new ResponseEntity<>(getAllRecentFolderResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(getAllRecentFolderResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllRecentFiles")
	public ResponseEntity<List<GetAllRecentFilesResponse>> getAllRecentFolders() {
		List<GetAllRecentFilesResponse> getAllRecentFilesResponse = null;
		try {
			getAllRecentFilesResponse = homeServiceImpl.findAllRecentFiles();
			if (!getAllRecentFilesResponse.isEmpty()) {
				return new ResponseEntity<>(getAllRecentFilesResponse, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(getAllRecentFilesResponse, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllSearchFiles/{fileName}")
	public ResponseEntity<List<SearchFilesResponse>> getAllSearchfile(@PathVariable String fileName) {
		List<SearchFilesResponse> searchFilesResponses = null;
		try {
			searchFilesResponses = homeServiceImpl.findAllSearchFiles(fileName);
			if (!searchFilesResponses.isEmpty()) {
				return new ResponseEntity<>(searchFilesResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(searchFilesResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllSearchFolders/{folderName}")
	public ResponseEntity<List<SearchFolderResponse>> getAllSearchFlder(@PathVariable String folderName) {
		List<SearchFolderResponse> searchFoldersResponses = null;
		try {
			searchFoldersResponses = homeServiceImpl.findAllSearchFolders(folderName);
			if (!searchFoldersResponses.isEmpty()) {
				return new ResponseEntity<>(searchFoldersResponses, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(searchFoldersResponses, HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateInactiveFile")
	public ResponseEntity<Map<String, String>> inactiveFiles(@RequestParam String id,@RequestParam String status) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.updateFiles(id,status);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/updateInactiveFolder")
	public ResponseEntity<Map<String, String>> inactiveFolder(@RequestParam String id,@RequestParam String status) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.updateFolder(id,status);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllInActiveFolders")
	public ResponseEntity<List<FolderPathResponse>> getAllInActiveFolders() {
		List<FolderPathResponse> response = null;
		try {
			response = homeServiceImpl.getAllInActive();
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAllInActiveFiles")
	public ResponseEntity<List<Files>> getAllInActiveFiles() {
		List<Files> response = null;
		try {
			response = homeServiceImpl.getAllInActiveFi();
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/saveCheckIn")
	public ResponseEntity<Map<String, String>> addCheckIn(@RequestParam int id,@RequestParam String folderName,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.addCheckIn(id,folderName, token);
			if (response.get(DMSConstant.STATUS).equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@PutMapping("/saveCheckOut")
	public ResponseEntity<Map<String, String>> addCheckOut(@RequestParam int id,@RequestParam String folderName,
			@RequestHeader("token") String token) {
		Map<String, String> response = new HashMap<>();
		try {
			response = homeServiceImpl.addCheckOut(id,folderName, token);
			if (response.get(DMSConstant.STATUS).equals(DMSConstant.SUCCESS)) {
				return ResponseEntity.ok(response);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
