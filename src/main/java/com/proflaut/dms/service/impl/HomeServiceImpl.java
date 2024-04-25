package com.proflaut.dms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
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
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class HomeServiceImpl {

	HomeHelper homeHelper;
	BookmarkRepository bookmarkRepository;
	ProfUserPropertiesRepository userPropertiesRepository;
	FileBookmarkRepository fileBookmarkRepository;
	ProfDocUploadRepository docUploadRepository;

	@Autowired
	public HomeServiceImpl(HomeHelper homeHelper, BookmarkRepository bookmarkRepository,
			ProfUserPropertiesRepository userPropertiesRepository, FileBookmarkRepository fileBookmarkRepository,
			ProfDocUploadRepository docUploadRepository) {
		this.homeHelper = homeHelper;
		this.bookmarkRepository = bookmarkRepository;
		this.userPropertiesRepository = userPropertiesRepository;
		this.fileBookmarkRepository = fileBookmarkRepository;
		this.docUploadRepository = docUploadRepository;
	}

	private static final Logger logger = LogManager.getLogger(HomeServiceImpl.class);

	public Map<String, String> saveBookmark(FolderBookmarkRequest bookmarkRequest, String token) {
		Map<String, String> response = new HashMap<>();
		try {
			ProfUserPropertiesEntity propertiesEntity = userPropertiesRepository.findByToken(token);
			if (!propertiesEntity.getToken().isEmpty()) {
				ProfFolderBookMarkEntity bookMarkEntity = homeHelper.convertRequestToEntity(bookmarkRequest,
						propertiesEntity);
				bookmarkRepository.save(bookMarkEntity);
				response.put("Status", DMSConstant.SUCCESS);
			} else {

				response.put("Failure", DMSConstant.FAILURE);
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
				ProfFileBookmarkEntity fileBookMarkEntity = homeHelper.convertRequestToFileEntity(fileBookMarkRequest,
						propertiesEntity);
				fileBookmarkRepository.save(fileBookMarkEntity);
				response.put("Status", DMSConstant.SUCCESS);
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
				List<Integer> fileIds = fileBookmarkEntities.stream().map(ProfFileBookmarkEntity::getFileId)
						.collect(Collectors.toList());
				for (int integer : fileIds) {
					ProfDocEntity fileSize = docUploadRepository.findById(integer);
					List<FileBookmark> fileBookmarks = homeHelper.mapToFileBookmarks(fileBookmarkEntities,fileSize);
					bookmarkResponse.setFiles(fileBookmarks);
				}

				// Fetch folder bookmarks for the user
				List<ProfFolderBookMarkEntity> folderBookmarkEntities = bookmarkRepository
						.findByBookMarkedBy(profUserPropertiesEntity.getUserName());
				List<Integer> folderIds = folderBookmarkEntities.stream().map(ProfFolderBookMarkEntity::getFolderId)
						.collect(Collectors.toList());
				for (Integer integer : folderIds) {
					List<ProfDocEntity> folderSize = docUploadRepository.findByFolderId(integer);
					long size = getTotalFileSize(folderSize);
					List<FolderBookmark> folderBookmarks = homeHelper.mapToFolderBookmarks(folderBookmarkEntities,
							size);

					bookmarkResponse.setFolders(folderBookmarks);

				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return bookmarkResponse;
	}

	public long getTotalFileSize(List<ProfDocEntity> docEntities) {
		long totalFileSize = 0;
		for (ProfDocEntity docEntity : docEntities) {
			String fileSizeString = docEntity.getFileSize();
			String numericValue = fileSizeString.replaceAll("[^\\d.]", "");
			long fileSize = Long.parseLong(numericValue);
			totalFileSize += fileSize;
		}
		return totalFileSize;
	}
}
