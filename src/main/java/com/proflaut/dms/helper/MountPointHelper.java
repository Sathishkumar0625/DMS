package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfMountPointEntity;
import com.proflaut.dms.entity.ProfMountPointFolderMappingEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.ProfMountFolderMappingRequest;
import com.proflaut.dms.model.ProfMountPointOverallResponse;
import com.proflaut.dms.model.ProfMountPointRequest;

@Component
public class MountPointHelper {

	public static String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm:ss ");
		return currentDateTime.format(formatter);
	}

	public ProfMountPointEntity convertRequestToMountPointEntity(ProfMountPointRequest pointRequest,
			ProfUserPropertiesEntity ent) {
		ProfMountPointEntity entity = new ProfMountPointEntity();
		entity.setCreatedAt(formatCurrentDateTime());
		entity.setCreatedBy(ent.getUserName());
		entity.setPath(pointRequest.getPath());
		entity.setStatus("A");
		return entity;
	}

	public ProfMountPointOverallResponse convertToResponse(ProfMountPointEntity mountPointEntity) {
		ProfMountPointOverallResponse overallResponse = new ProfMountPointOverallResponse();
		overallResponse.setId(mountPointEntity.getId());
		overallResponse.setCreatedAt(mountPointEntity.getCreatedAt());
		overallResponse.setCreatedBy(mountPointEntity.getCreatedBy());
		overallResponse.setPath(mountPointEntity.getPath());
		overallResponse.setStatus(mountPointEntity.getStatus());

		return overallResponse;
	}

	public ProfMountPointOverallResponse convertRequestToResponse(ProfMountPointEntity mountPointEntities) {
		ProfMountPointOverallResponse overallResponse = new ProfMountPointOverallResponse();
		overallResponse.setId(mountPointEntities.getId());
		overallResponse.setCreatedAt(mountPointEntities.getCreatedAt());
		overallResponse.setCreatedBy(mountPointEntities.getCreatedBy());
		overallResponse.setPath(mountPointEntities.getPath());
		overallResponse.setStatus(mountPointEntities.getStatus());

		return overallResponse;
	}
	
	public ProfMountPointFolderMappingEntity convertRequestToMappingEntity(
			ProfMountFolderMappingRequest folderMappingRequest, ProfUserPropertiesEntity entity2) {
		ProfMountPointFolderMappingEntity entity = new ProfMountPointFolderMappingEntity();
		entity.setCreatedAt(formatCurrentDateTime());
		entity.setCreatedBy(entity2.getUserName());
		entity.setFolderId(folderMappingRequest.getFolderId());
		entity.setMountPointId(folderMappingRequest.getMountPointId());
		return entity;
	}

	public FolderPathResponse convertRequestToFolderResponse(FolderEntity folderEntity) {
		FolderPathResponse folderPathResponse=new FolderPathResponse();
		folderPathResponse.setFolderID(folderEntity.getId());
		folderPathResponse.setFolderName(folderEntity.getFolderName());
		return folderPathResponse;
	}

}
