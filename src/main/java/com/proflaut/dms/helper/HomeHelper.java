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
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FileBookmark;
import com.proflaut.dms.model.FolderBookmark;
import com.proflaut.dms.model.FolderBookmarkRequest;
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
		return bookMarkEntity;
	}

	public ProfFileBookmarkEntity convertRequestToFileEntity(FileBookMarkRequest fileBookMarkRequest,
			ProfUserPropertiesEntity propertiesEntity) {
		ProfFileBookmarkEntity bookMarkEntity = new ProfFileBookmarkEntity();
		bookMarkEntity.setFileName(fileBookMarkRequest.getFileName());
		bookMarkEntity.setFileId(Integer.parseInt(fileBookMarkRequest.getFileId()));
		bookMarkEntity.setBookmarkDateAndTime(formatCurrentDateTime());
		bookMarkEntity.setBookmarkedBy(propertiesEntity.getUserName());
		return bookMarkEntity;
	}

//	public List<FileBookmark> mapToFileBookmarks(List<ProfFileBookmarkEntity> fileBookmarkEntities,
//			ProfDocEntity fileSize) {
//		List<FileBookmark> fileBookmarks = new ArrayList<>();
//		for (ProfFileBookmarkEntity entity : fileBookmarkEntities) {
//			FileBookmark fileBookmark = new FileBookmark();
//			fileBookmark.setFileName(entity.getFileName());
//			fileBookmark.setFileId(String.valueOf(entity.getFileId()));
//			fileBookmark.setBookmarkedBy(entity.getBookmarkedBy());
//			fileBookmark.setBookmarkDateAndTime(entity.getBookmarkDateAndTime());
//			fileBookmark.setFileSize(fileSize.getFileSize());
//			fileBookmarks.add(fileBookmark);
//		}
//		return fileBookmarks;
//	}
//
//	public List<FolderBookmark> mapToFolderBookmarks(List<ProfFolderBookMarkEntity> folderBookmarkEntities,
//			long totalSizeKB) {
//		List<FolderBookmark> folderBookmarks = new ArrayList<>();
//		for (ProfFolderBookMarkEntity entity : folderBookmarkEntities) {
//			FolderBookmark folderBookmark = new FolderBookmark();
//			folderBookmark.setFolderId(String.valueOf(entity.getFolderId()));
//			folderBookmark.setFolderName(entity.getFolderName());
//			folderBookmark.setBookmarkedBy(entity.getBookMarkedBy());
//			folderBookmark.setBookmarkDateAndTime(entity.getBookamrkDateAndTime());
//			folderBookmark.setFolderSize(totalSizeKB + "kb");
//			folderBookmarks.add(folderBookmark);
//		}
//		return folderBookmarks;
//	}
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
			ProfDocEntity fileSize = fileSizeMap.get(entity.getFileId());
			FileBookmark fileBookmark = new FileBookmark();
			fileBookmark.setFileName(entity.getFileName());
			fileBookmark.setFileId(String.valueOf(entity.getFileId()));
			fileBookmark.setBookmarkedBy(entity.getBookmarkedBy());
			fileBookmark.setBookmarkDateAndTime(entity.getBookmarkDateAndTime());
			if (fileSize != null) {
				fileBookmark.setFileSize(fileSize.getFileSize());
				fileBookmark.setFileUploadeddateAndTime(fileSize.getUploadTime());
			}
			fileBookmarks.add(fileBookmark);
		}
		return fileBookmarks;
	}

	public List<FolderBookmark> mapToFolderBookmarks(List<ProfFolderBookMarkEntity> folderBookmarkEntities) {
		List<FolderBookmark> folderBookmarks = new ArrayList<>();
		for (ProfFolderBookMarkEntity entity : folderBookmarkEntities) {
			List<ProfDocEntity> folderFiles = docUploadRepository.findByFolderId(entity.getFolderId());
			long totalSizeKB = getTotalFileSize(folderFiles);
			FolderEntity folderEntity = folderRepository.findById(entity.getFolderId());
			FolderBookmark folderBookmark = new FolderBookmark();
			folderBookmark.setFolderId(String.valueOf(entity.getFolderId()));
			folderBookmark.setFolderName(entity.getFolderName());
			folderBookmark.setBookmarkedBy(entity.getBookMarkedBy());
			folderBookmark.setBookmarkDateAndTime(entity.getBookamrkDateAndTime());
			folderBookmark.setFolderSize(totalSizeKB + "kb");
			folderBookmark.setFolderCreatedDateAndTime(folderEntity.getCreatedAt());
			folderBookmarks.add(folderBookmark);
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

	public long parseSizeStringToKB(String sizeString) {
		String[] parts = sizeString.split(" ");
		if (parts.length == 2) {
			long size = Long.parseLong(parts[0]);
			String unit = parts[1].toLowerCase();
			if (unit.equals("kb")) {
				return size;
			} else if (unit.equals("mb")) {
				return size * 1024;
			} else if (unit.equals("gb")) {
				return size * 1024 * 1024;
			} else {
				throw new IllegalArgumentException("Unsupported unit: " + unit);
			}
		} else {
			throw new IllegalArgumentException("Invalid size string format: " + sizeString);
		}
	}
}
