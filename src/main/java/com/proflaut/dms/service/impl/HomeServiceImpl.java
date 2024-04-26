package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfRecentFileEntity;
import com.proflaut.dms.entity.ProfRecentFilePropertyEntity;
import com.proflaut.dms.entity.ProfRecentFolderPropertyEntity;
import com.proflaut.dms.entity.ProfRecentFoldersEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.helper.HomeHelper;
import com.proflaut.dms.model.BookmarkResponse;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FileBookmark;
import com.proflaut.dms.model.FolderBookmark;
import com.proflaut.dms.model.FolderBookmarkRequest;
import com.proflaut.dms.model.GetAllRecentFilesResponse;
import com.proflaut.dms.model.GetAllRecentFolderResponse;
import com.proflaut.dms.model.SearchFilesResponse;
import com.proflaut.dms.model.SearchFolderResponse;
import com.proflaut.dms.repository.BookmarkRepository;
import com.proflaut.dms.repository.FileBookmarkRepository;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfRecentFileRepository;
import com.proflaut.dms.repository.ProfRecentFilesPropertyRepository;
import com.proflaut.dms.repository.ProfRecentFoldersPropertyRepository;
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
	ProfRecentFilesPropertyRepository filesPropertyRepository;
	ProfRecentFoldersPropertyRepository foldersPropertyRepository;
	FolderRepository folderRepository;

	@Autowired
	public HomeServiceImpl(HomeHelper homeHelper, BookmarkRepository bookmarkRepository,
			ProfUserPropertiesRepository userPropertiesRepository, FileBookmarkRepository fileBookmarkRepository,
			ProfDocUploadRepository docUploadRepository, ProfRecentFoldersRepository profRecentFoldersRepository,
			ProfRecentFileRepository profRecentFileRepository,
			ProfRecentFilesPropertyRepository filesPropertyRepository,
			ProfRecentFoldersPropertyRepository foldersPropertyRepository,FolderRepository folderRepository) {
		this.homeHelper = homeHelper;
		this.bookmarkRepository = bookmarkRepository;
		this.userPropertiesRepository = userPropertiesRepository;
		this.fileBookmarkRepository = fileBookmarkRepository;
		this.docUploadRepository = docUploadRepository;
		this.profRecentFoldersRepository = profRecentFoldersRepository;
		this.profRecentFileRepository = profRecentFileRepository;
		this.filesPropertyRepository = filesPropertyRepository;
		this.foldersPropertyRepository = foldersPropertyRepository;
		this.folderRepository=folderRepository;
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
					ProfFileBookmarkEntity fileBookMarkEntity = homeHelper
							.convertRequestToFileEntity(fileBookMarkRequest, propertiesEntity);
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
			long count = foldersPropertyRepository.count();
			if (count < 50) {
				ProfRecentFolderPropertyEntity folderPropertyEntity = homeHelper
						.convertRequestToFolderProperty(bookmarkRequest, profUserPropertiesEntity);
				foldersPropertyRepository.save(folderPropertyEntity);
			} else {
				foldersPropertyRepository.deleteAll();
				ProfRecentFolderPropertyEntity folderPropertyEntity = homeHelper
						.convertRequestToFolderProperty(bookmarkRequest, profUserPropertiesEntity);
				foldersPropertyRepository.save(folderPropertyEntity);
			}
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
			long count = filesPropertyRepository.count();
			if (count < 50) {
				ProfRecentFilePropertyEntity folderPropertyEntity = homeHelper
						.convertRequestToRecentFilePropertyEntity(fileBookmarkRequest, profUserPropertiesEntity);
				filesPropertyRepository.save(folderPropertyEntity);
			} else {
				filesPropertyRepository.deleteAll();
				ProfRecentFilePropertyEntity folderPropertyEntity = homeHelper
						.convertRequestToRecentFilePropertyEntity(fileBookmarkRequest, profUserPropertiesEntity);
				filesPropertyRepository.save(folderPropertyEntity);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return response;
	}

	public List<GetAllRecentFolderResponse> findAllRecentFolders(String token) {
		List<GetAllRecentFolderResponse> allRecentFolderResponses = new ArrayList<>();
		try {
			List<ProfRecentFolderPropertyEntity> folderPropertyEntites = foldersPropertyRepository.findAllByOrderByIdDesc();
			if (!folderPropertyEntites.isEmpty()) {
				for (ProfRecentFolderPropertyEntity profRecentFolderPropertyEntity : folderPropertyEntites) {
					GetAllRecentFolderResponse folderResponse = homeHelper
							.convertFolderPropertyToResponse(profRecentFolderPropertyEntity);
					allRecentFolderResponses.add(folderResponse);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return allRecentFolderResponses;
	}

	public List<GetAllRecentFilesResponse> findAllRecentFiles() {
		List<GetAllRecentFilesResponse> allRecentFilesResponses = new ArrayList<>();
		try {
			List<ProfRecentFilePropertyEntity> filerPropertyEntites = filesPropertyRepository.findAllByOrderByIdDesc();
			if (!filerPropertyEntites.isEmpty()) {
				for (ProfRecentFilePropertyEntity filePropertyEntity : filerPropertyEntites) {
					GetAllRecentFilesResponse folderResponse = homeHelper
							.convertFilePropertyToResponse(filePropertyEntity);
					allRecentFilesResponses.add(folderResponse);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return allRecentFilesResponses;
	}

	public List<SearchFilesResponse> findAllSearchFiles(String fileName) {
		List<SearchFilesResponse> filesResponses = new ArrayList<>();
		try {
			List<ProfDocEntity> profDocEntity = docUploadRepository.findByDocNameLike("%" + fileName + "%");
			for (ProfDocEntity profDocEnt : profDocEntity) {
				SearchFilesResponse filesResponse = homeHelper.convertToSearchFilesResponse(profDocEnt);
				filesResponses.add(filesResponse);
			}

		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return filesResponses;
	}

	public List<SearchFolderResponse> findAllSearchFolders(String folderName) {
		List<SearchFolderResponse> folderResponses = new ArrayList<>();
		try {
			List<FolderEntity> folderEntities = folderRepository.findByFolderNameLike("%" + folderName + "%");
			for (FolderEntity folderEntity : folderEntities) {
				SearchFolderResponse folderResponse = homeHelper.convertToSearchFolderResponse(folderEntity);
				folderResponses.add(folderResponse);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return folderResponses;
	}
}
