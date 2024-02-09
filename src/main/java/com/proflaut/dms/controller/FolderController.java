package com.proflaut.dms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.helper.FolderHelper;
import com.proflaut.dms.model.FileRequest;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderRetreiveResponse;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.service.impl.FileManagementServiceImpl;
import com.proflaut.dms.service.interF.FolderService;

@RestController
@RequestMapping("/folder")
@CrossOrigin
public class FolderController {

	@Autowired
	FolderService folderService;
	
	@Autowired
	FileManagementServiceImpl serviceIMPL;
	
	@Autowired
	FolderHelper folderHelper;


	@PostMapping("/create")
	public ResponseEntity<FileResponse> createFolder(@RequestBody FolderFO folderFO) {

		FileResponse fileResponse = new FileResponse();
		try {
			fileResponse = folderService.saveFolder(folderFO);
			if (DMSConstant.SUCCESS.equals(fileResponse.getStatus())) {
	            return new ResponseEntity<>(fileResponse, HttpStatus.CREATED);
	        } else {
	            return new ResponseEntity<>(fileResponse, HttpStatus.NOT_ACCEPTABLE);
	        }

		} catch (Exception e) {

			fileResponse.setStatus(DMSConstant.FAILURE);
			fileResponse.setProspectId(folderFO.getProspectId());
			if (e.getMessage().contains(DMSConstant.CONSTRAINTVIOLATIONEXCEPTION)) {
				fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
			} else {
				fileResponse.setErrorMessage(e.getMessage());

			}
			return new ResponseEntity<>(fileResponse, HttpStatus.NOT_ACCEPTABLE);

		}
	}

	@GetMapping("/ById/{id}")
	public ResponseEntity<FileResponse> getById(@PathVariable("id") Integer id, FileRequest fileRequest) {
		FileResponse fileResponse = null;
		try {
			fileResponse = folderService.retriveFile(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fileResponse != null && fileResponse.getProspectId() != null) {
			
			return new ResponseEntity<>(fileResponse, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/getById/{id}")
	public ResponseEntity<FolderRetreiveResponse> findById(@PathVariable("id") Integer id){
		try {
		FolderRetreiveResponse folderRetreiveResponse = folderService.retreive(id);
		List<Folders> document = folderRetreiveResponse.getFolder();
		if (document != null && folderRetreiveResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
            return new ResponseEntity<>(folderRetreiveResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } catch (Exception e) {
        e.printStackTrace(); 
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
		
}
	@GetMapping("/getAll")
	public ResponseEntity<FolderRetreiveResponse> getAll() {
	    try {
	        FolderRetreiveResponse folderRetreiveResponse = folderService.getAllFolders();
	        if (folderRetreiveResponse != null && folderRetreiveResponse.getStatus().equalsIgnoreCase(DMSConstant.SUCCESS)) {
	            return new ResponseEntity<>(folderRetreiveResponse, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

}
