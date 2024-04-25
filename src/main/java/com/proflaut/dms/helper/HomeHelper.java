package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfFileBookmarkEntity;
import com.proflaut.dms.entity.ProfFolderBookMarkEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FileBookMarkRequest;
import com.proflaut.dms.model.FileBookmark;
import com.proflaut.dms.model.FolderBookmark;
import com.proflaut.dms.model.FolderBookmarkRequest;

@Component
public class HomeHelper {

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

	public List<FileBookmark> mapToFileBookmarks(List<ProfFileBookmarkEntity> fileBookmarkEntities,
			ProfDocEntity fileSize) {
		List<FileBookmark> fileBookmarks = new ArrayList<>();
		for (ProfFileBookmarkEntity entity : fileBookmarkEntities) {
			FileBookmark fileBookmark = new FileBookmark();
			fileBookmark.setFileName(entity.getFileName());
			fileBookmark.setFileId(String.valueOf(entity.getFileId()));
			fileBookmark.setBookmarkedBy(entity.getBookmarkedBy());
			fileBookmark.setBookmarkDateAndTime(entity.getBookmarkDateAndTime());
			fileBookmark.setFileSize(fileSize.getFileSize());
			fileBookmarks.add(fileBookmark);
		}
		return fileBookmarks;
	}

	public List<FolderBookmark> mapToFolderBookmarks(List<ProfFolderBookMarkEntity> folderBookmarkEntities,
			long totalSizeKB) {
		List<FolderBookmark> folderBookmarks = new ArrayList<>();
		for (ProfFolderBookMarkEntity entity : folderBookmarkEntities) {
			FolderBookmark folderBookmark = new FolderBookmark();
			folderBookmark.setFolderId(String.valueOf(entity.getFolderId()));
			folderBookmark.setFolderName(entity.getFolderName());
			folderBookmark.setBookmarkedBy(entity.getBookMarkedBy());
			folderBookmark.setBookmarkDateAndTime(entity.getBookamrkDateAndTime());
			folderBookmark.setFolderSize(totalSizeKB + "kb");
			folderBookmarks.add(folderBookmark);
		}
		return folderBookmarks;
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
