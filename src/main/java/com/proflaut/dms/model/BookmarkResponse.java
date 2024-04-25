package com.proflaut.dms.model;

import java.util.List;

import lombok.Data;

@Data
public class BookmarkResponse {
	private List<FileBookmark> files;
	private List<FolderBookmark> folders;

}
