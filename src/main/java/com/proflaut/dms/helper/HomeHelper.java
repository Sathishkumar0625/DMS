package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfCheckInAndOutEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfRecentFileEntity;
import com.proflaut.dms.entity.ProfRecentFilePropertyEntity;
import com.proflaut.dms.entity.ProfRecentFolderPropertyEntity;
import com.proflaut.dms.entity.ProfRecentFoldersEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FileBookmark;
import com.proflaut.dms.model.Files;
import com.proflaut.dms.model.FolderBookmark;
import com.proflaut.dms.model.FolderBookmarkRequest;
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.GetAllRecentFilesResponse;
import com.proflaut.dms.model.GetAllRecentFolderResponse;
import com.proflaut.dms.model.SearchFilesResponse;
import com.proflaut.dms.model.SearchFolderResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;

@Component
public class HomeHelper {

	ProfDocUploadRepository docUploadRepository;
	FolderRepository folderRepository;

	@Autowired
	public HomeHelper(ProfDocUploadRepository docUploadRepository, FolderRepository folderRepository) {
		this.docUploadRepository = docUploadRepository;
		this.folderRepository = folderRepository;
	}

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public ProfFolderBookMarkEntity convertRequestToEntity(FolderBookmarkRequest bookmarkRequest,
			ProfUserPropertiesEntity propertiesEntity) {
		ProfFolderBookMarkEntity bookMarkEntity = new ProfFolderBookMarkEntity();
		bookMarkEntity.setFolderId(Integer.parseInt(bookmarkRequest.getFolderId()));
		bookMarkEntity.setFolderName(bookmarkRequest.getFolderName());
		bookMarkEntity.setBookamrkDateAndTime(formatCurrentDateTime());
		bookMarkEntity.setBookMarkedBy(propertiesEntity.getUserName());
		bookMarkEntity.setBookmark(bookmarkRequest.getBookmark());
		return bookMarkEntity;
	}

	public ProfFileBookmarkEntity convertRequestToFileEntity(FileBookMarkRequest fileBookMarkRequest,
			ProfUserPropertiesEntity propertiesEntity) {
		ProfFileBookmarkEntity bookMarkEntity = new ProfFileBookmarkEntity();
		bookMarkEntity.setFileName(fileBookMarkRequest.getFileName());
		bookMarkEntity.setFileId(Integer.parseInt(fileBookMarkRequest.getFileId()));
		bookMarkEntity.setBookmarkDateAndTime(formatCurrentDateTime());
		bookMarkEntity.setBookmarkedBy(propertiesEntity.getUserName());
		bookMarkEntity.setBookmark("YES");
		return bookMarkEntity;
	}

	public Map<Integer, ProfDocEntity> getFileSizesForFiles(List<ProfFileBookmarkEntity> fileBookmarkEntities) {
		List<Integer> fileIds = fileBookmarkEntities.stream().map(ProfFileBookmarkEntity::getFileId)
				.collect(Collectors.toList());
		List<ProfDocEntity> fileSizes = docUploadRepository.findByIdIn(fileIds);
		return fileSizes.stream().collect(Collectors.toMap(ProfDocEntity::getId, Function.identity()));
	}

	public List<FileBookmark> mapToFileBookmarks(List<ProfFileBookmarkEntity> fileBookmarkEntities,
			Map<Integer, ProfDocEntity> fileSizeMap) {
		List<FileBookmark> fileBookmarks = new ArrayList<>();
		for (ProfFileBookmarkEntity entity : fileBookmarkEntities) {
			if (entity.getBookmark().equalsIgnoreCase("YES")) {
				ProfDocEntity fileSize = fileSizeMap.get(entity.getFileId());
				FileBookmark fileBookmark = new FileBookmark();
				fileBookmark.setName(entity.getFileName());
				fileBookmark.setFileId(String.valueOf(entity.getFileId()));
				fileBookmark.setBookmarkedBy(entity.getBookmarkedBy());
				fileBookmark.setBookmarkDateAndTime(entity.getBookmarkDateAndTime());
				if (fileSize != null) {
					fileBookmark.setSize(fileSize.getFileSize());
					fileBookmark.setFileUploadeddateAndTime(fileSize.getUploadTime());
				}
				fileBookmarks.add(fileBookmark);
			}
		}
		return fileBookmarks;
	}

	public List<FolderBookmark> mapToFolderBookmarks(List<ProfFolderBookMarkEntity> folderBookmarkEntities) {
		List<FolderBookmark> folderBookmarks = new ArrayList<>();
		for (ProfFolderBookMarkEntity entity : folderBookmarkEntities) {
			if (entity.getBookmark().equalsIgnoreCase("YES")) {
				List<ProfDocEntity> folderFiles = docUploadRepository.findByFolderId(entity.getFolderId());
				long totalSizeKB = getTotalFileSize(folderFiles);
				FolderEntity folderEntity = folderRepository.findById(entity.getFolderId());
				FolderBookmark folderBookmark = new FolderBookmark();
				folderBookmark.setFolderId(String.valueOf(entity.getFolderId()));
				folderBookmark.setName(entity.getFolderName());
				folderBookmark.setBookmarkedBy(entity.getBookMarkedBy());
				folderBookmark.setBookmarkDateAndTime(entity.getBookamrkDateAndTime());
				folderBookmark.setSize(totalSizeKB + "kb");
				folderBookmark.setFolderCreatedDateAndTime(folderEntity.getCreatedAt());
				folderBookmarks.add(folderBookmark);
			}

		}
		return folderBookmarks;
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

	public ProfRecentFoldersEntity convertRequestToRecentEntity(FolderBookmarkRequest bookmarkRequest,
			ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfRecentFoldersEntity profRecentFoldersEntity = new ProfRecentFoldersEntity();
		profRecentFoldersEntity.setFolderName(bookmarkRequest.getFolderName());
		profRecentFoldersEntity.setFolderId(bookmarkRequest.getFolderId());
		profRecentFoldersEntity.setAddedBy(profUserPropertiesEntity.getUserName());
		profRecentFoldersEntity.setAddedOn(formatCurrentDateTime());
		return profRecentFoldersEntity;
	}

	public ProfRecentFileEntity convertRequestToRecentFileEntity(FileBookMarkRequest fileBookmarkRequest,
			ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfRecentFileEntity profRecentFileEntity = new ProfRecentFileEntity();
		profRecentFileEntity.setFileName(fileBookmarkRequest.getFileName());
		profRecentFileEntity.setFileId(Integer.parseInt(fileBookmarkRequest.getFileId()));
		profRecentFileEntity.setAddedBy(profUserPropertiesEntity.getUserName());
		profRecentFileEntity.setAddedOn(formatCurrentDateTime());
		return profRecentFileEntity;
	}

	public ProfRecentFolderPropertyEntity convertRequestToFolderProperty(FolderBookmarkRequest bookmarkRequest,
			ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfRecentFolderPropertyEntity folderPropertyEntity = new ProfRecentFolderPropertyEntity();
		folderPropertyEntity.setFolderName(bookmarkRequest.getFolderName());
		folderPropertyEntity.setFolderId(bookmarkRequest.getFolderId());
		folderPropertyEntity.setAddedBy(profUserPropertiesEntity.getUserName());
		folderPropertyEntity.setAddedOn(formatCurrentDateTime());
		return folderPropertyEntity;
	}

	public ProfRecentFilePropertyEntity convertRequestToRecentFilePropertyEntity(
			FileBookMarkRequest fileBookmarkRequest, ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfRecentFilePropertyEntity filePropertyEntity = new ProfRecentFilePropertyEntity();
		filePropertyEntity.setFileName(fileBookmarkRequest.getFileName());
		filePropertyEntity.setFileId(Integer.parseInt(fileBookmarkRequest.getFileId()));
		filePropertyEntity.setAddedBy(profUserPropertiesEntity.getUserName());
		filePropertyEntity.setAddedOn(formatCurrentDateTime());
		return filePropertyEntity;
	}

	public GetAllRecentFolderResponse convertFolderPropertyToResponse(
			ProfRecentFolderPropertyEntity profRecentFolderPropertyEntity) {
		GetAllRecentFolderResponse folderResponse = new GetAllRecentFolderResponse();
		folderResponse.setId(profRecentFolderPropertyEntity.getId());
		folderResponse.setAddedBy(profRecentFolderPropertyEntity.getAddedBy());
		folderResponse.setAddedOn(profRecentFolderPropertyEntity.getAddedOn());
		folderResponse.setIds(profRecentFolderPropertyEntity.getFolderId());
		folderResponse.setName(profRecentFolderPropertyEntity.getFolderName());
		return folderResponse;
	}

	public GetAllRecentFilesResponse convertFilePropertyToResponse(ProfRecentFilePropertyEntity filePropertyEntity) {
		GetAllRecentFilesResponse filesResponse = new GetAllRecentFilesResponse();
		filesResponse.setName(filePropertyEntity.getFileName());
		filesResponse.setIds(filePropertyEntity.getFileId());
		filesResponse.setAddedBy(filePropertyEntity.getAddedBy());
		filesResponse.setAddedOn(filePropertyEntity.getAddedOn());
		filesResponse.setId(filePropertyEntity.getId());
		return filesResponse;
	}

	public SearchFilesResponse convertToSearchFilesResponse(ProfDocEntity profDocEnt) {
		SearchFilesResponse response = new SearchFilesResponse();
		response.setFileSize(profDocEnt.getFileSize());
		response.setCreatedBy(profDocEnt.getCreatedBy());
		response.setExtention(profDocEnt.getExtention());
		response.setDocName(profDocEnt.getDocName());
		response.setUploadtime(profDocEnt.getUploadTime());
		response.setId(profDocEnt.getId());
		return response;
	}

	public SearchFolderResponse convertToSearchFolderResponse(FolderEntity folderEntity) {
		SearchFolderResponse folderResponse = new SearchFolderResponse();
		folderResponse.setDocName(folderEntity.getFolderName());
		folderResponse.setUploadTime(folderEntity.getCreatedAt());
		folderResponse.setCreatedBy(folderEntity.getCreatedBy());
		folderResponse.setId(folderEntity.getId());
		return folderResponse;
	}

	public FolderPathResponse convertToInactiveFolderResponse(FolderEntity folderEntity) {
		FolderPathResponse response = new FolderPathResponse();
		response.setName(folderEntity.getFolderName());
		response.setFolderPath(folderEntity.getIsParent());
		response.setCreatedBy(folderEntity.getCreatedBy());
		response.setCreatedAt(folderEntity.getCreatedAt());
		response.setFolderID(String.valueOf(folderEntity.getId()));
		return response;
	}

	public Files convertToInactiveFiles(ProfDocEntity profDocEntity) {
		Files files = new Files();
		files.setName(profDocEntity.getDocName());
		files.setCreatedAt(profDocEntity.getUploadTime());
		files.setCreatedBy(profDocEntity.getCreatedBy());
		files.setId(profDocEntity.getId());
		return files;
	}

	public ProfCheckInAndOutEntity convertToCheckInOutEnty(int id, String folderName,
			ProfUserPropertiesEntity profUserPropertiesEntity) {
		ProfCheckInAndOutEntity entity = new ProfCheckInAndOutEntity();
		entity.setFolderName(folderName);
		entity.setUserId(profUserPropertiesEntity.getUserId());
		entity.setCheckIn("YES");
		entity.setCheckOut("NO");
		entity.setCheckInBy(profUserPropertiesEntity.getUserName());
		entity.setFolderId(id);
		entity.setCheckInTime(formatCurrentDateTime());
		return entity;
	}

	public ProfCheckInAndOutEntity convertTocheckoutEntity(ProfCheckInAndOutEntity andOutEntity) {
		andOutEntity.setCheckIn("NO");
		andOutEntity.setCheckOut("YES");
		andOutEntity.setCheckOutTime(formatCurrentDateTime());
		return andOutEntity;
	}

	public FolderEntity convertCheckOutEntity(FolderEntity folderEntity) {
		folderEntity.setCheckIn("NO");
		folderEntity.setCheckOut("YES");
		folderEntity.setCheckOutTime(formatCurrentDateTime());
		return folderEntity;
	}

}
