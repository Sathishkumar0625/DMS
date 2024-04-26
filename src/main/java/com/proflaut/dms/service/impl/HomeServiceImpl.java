package com.proflaut.dms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfRecentFileEntity;
import com.proflaut.dms.entity.ProfRecentFoldersEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.helper.HomeHelper;
import com.proflaut.dms.model.BookmarkResponse;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FileBookmark;
import com.proflaut.dms.model.FolderBookmark;
import com.proflaut.dms.model.FolderBookmarkRequest;
import com.proflaut.dms.repository.BookmarkRepository;
import com.proflaut.dms.repository.FileBookmarkRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfRecentFileRepository;
import com.proflaut.dms.repository.ProfRecentFoldersRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class HomeServiceImpl {

	HomeHelper homeHelper;
	BookmarkRepository bookmarkRepository;
	ProfUserPropertiesRepository userPropertiesRepository;
	FileBookmarkRepository fileBookmarkRepository;
	ProfDocUploadRepository docUploadRepository;
	ProfRecentFoldersRepository profRecentFoldersRepository;
	ProfRecentFileRepository profRecentFileRepository;

	@Autowired
	public HomeServiceImpl(HomeHelper homeHelper, BookmarkRepository bookmarkRepository,
			ProfUserPropertiesRepository userPropertiesRepository, FileBookmarkRepository fileBookmarkRepository,
			ProfDocUploadRepository docUploadRepository, ProfRecentFoldersRepository profRecentFoldersRepository,
			ProfRecentFileRepository profRecentFileRepository) {
		this.homeHelper = homeHelper;
		this.bookmarkRepository = bookmarkRepository;
		this.userPropertiesRepository = userPropertiesRepository;
		this.fileBookmarkRepository = fileBookmarkRepository;
		this.docUploadRepository = docUploadRepository;
		this.profRecentFoldersRepository = profRecentFoldersRepository;
		this.profRecentFileRepository = profRecentFileRepository;
	}

	private static final Logger logger = LogManager.getLogger(HomeServiceImpl.class);

	public Map<String, String> saveBookmark(FolderBookmarkRequest bookmarkRequest, String token) {
		Map<String, String> response = new HashMap<>();
		try {
			ProfUserPropertiesEntity propertiesEntity = userPropertiesRepository.findByToken(token);
			if (!propertiesEntity.getToken().isEmpty()) {
				if (bookmarkRequest.getBookmark().equalsIgnoreCase("YES")) {
					ProfFolderBookMarkEntity bookMarkEntity = homeHelper.convertRequestToEntity(bookmarkRequest,
							propertiesEntity);
					bookmarkRepository.save(bookMarkEntity);
					response.put(DMSConstant.STATUS, DMSConstant.SUCCESS);
				} else {
					ProfFolderBookMarkEntity bookMarkEntity = bookmarkRepository
							.findByFolderId(Integer.parseInt(bookmarkRequest.getFolderId()));
					bookMarkEntity.setBookmark("NO");
					bookmarkRepository.save(bookMarkEntity);
					response.put(DMSConstant.STATUS, DMSConstant.SUCCESS);
				}
			} else {

				response.put(DMSConstant.STATUSFAILURE, DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			response.put("Status", DMSConstant.FAILURE);
		}
		return response;
	}

	public Map<String, String> saveFileBookmark(FileBookMarkRequest fileBookMarkRequest, String token) {
		Map<String, String> response = new HashMap<>();
		try {
			ProfUserPropertiesEntity propertiesEntity = userPropertiesRepository.findByToken(token);
			if (!propertiesEntity.getToken().isEmpty()) {
				if (fileBookMarkRequest.getBookmark().equalsIgnoreCase("YES")) {
					ProfFileBookmarkEntity fileBookMarkEntity = homeHelper.convertRequestToFileEntity(fileBookMarkRequest,
							propertiesEntity);
					fileBookmarkRepository.save(fileBookMarkEntity);
					response.put("Status", DMSConstant.SUCCESS);
				} else {
					ProfFileBookmarkEntity bookMarkEntity = fileBookmarkRepository
							.findByFileId(Integer.parseInt(fileBookMarkRequest.getFileId()));
					bookMarkEntity.setBookmark("NO");
					fileBookmarkRepository.save(bookMarkEntity);
					response.put(DMSConstant.STATUS, DMSConstant.SUCCESS);
				}
			} else {

				response.put("Failure", DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			response.put("Status", DMSConstant.FAILURE);
		}
		return response;
	}

	public BookmarkResponse findAll(String token) {
		BookmarkResponse bookmarkResponse = new BookmarkResponse();
		try {
			ProfUserPropertiesEntity profUserPropertiesEntity = userPropertiesRepository.findByToken(token);
			if (profUserPropertiesEntity != null && !profUserPropertiesEntity.getToken().isBlank()) {
				// Fetch file bookmarks for the user
				List<ProfFileBookmarkEntity> fileBookmarkEntities = fileBookmarkRepository
						.findByBookmarkedBy(profUserPropertiesEntity.getUserName());

				// Fetch folder bookmarks for the user
				List<ProfFolderBookMarkEntity> folderBookmarkEntities = bookmarkRepository
						.findByBookMarkedBy(profUserPropertiesEntity.getUserName());

				// Map file sizes to file bookmark entities
				Map<Integer, ProfDocEntity> fileSizeMap = homeHelper.getFileSizesForFiles(fileBookmarkEntities);

				// Set file bookmarks
				List<FileBookmark> fileBookmarks = homeHelper.mapToFileBookmarks(fileBookmarkEntities, fileSizeMap);
				bookmarkResponse.setFiles(fileBookmarks);

				// Set folder bookmarks with total folder sizes
				List<FolderBookmark> folderBookmarks = homeHelper.mapToFolderBookmarks(folderBookmarkEntities);
				bookmarkResponse.setFolders(folderBookmarks);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return bookmarkResponse;
	}

	public Map<String, String> addRecentFol(FolderBookmarkRequest bookmarkRequest, String token) {
		Map<String, String> response = new HashMap<>();
		try {
			ProfUserPropertiesEntity profUserPropertiesEntity = userPropertiesRepository.findByToken(token);
			ProfRecentFoldersEntity profRecentFoldersEntity = homeHelper.convertRequestToRecentEntity(bookmarkRequest,
					profUserPropertiesEntity);
			profRecentFoldersRepository.save(profRecentFoldersEntity);
			response.put(DMSConstant.STATUS, DMSConstant.SUCCESS);
		} catch (Exception e) {
			response.put(DMSConstant.STATUSFAILURE, DMSConstant.FAILURE);
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return response;
	}

	public Map<String, String> addRecentFil(FileBookMarkRequest fileBookmarkRequest, String token) {
		Map<String, String> response = new HashMap<>();
		try {
			ProfUserPropertiesEntity profUserPropertiesEntity = userPropertiesRepository.findByToken(token);
			ProfRecentFileEntity profRecentFileEntity = homeHelper.convertRequestToRecentFileEntity(fileBookmarkRequest,
					profUserPropertiesEntity);
			profRecentFileRepository.save(profRecentFileEntity);
			response.put(DMSConstant.STATUS, DMSConstant.SUCCESS);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return response;
	}
}
