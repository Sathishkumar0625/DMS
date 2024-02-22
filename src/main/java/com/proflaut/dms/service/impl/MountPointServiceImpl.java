package com.proflaut.dms.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMountPointEntity;
import com.proflaut.dms.entity.ProfMountPointFolderMappingEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.MountPointHelper;
import com.proflaut.dms.model.FolderPathResponse;
import com.proflaut.dms.model.ProfMountFolderMappingRequest;
import com.proflaut.dms.model.ProfMountPointOverallResponse;
import com.proflaut.dms.model.ProfMountPointRequest;
import com.proflaut.dms.model.ProfMountPointResponse;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfMountFolderMappingRepository;
import com.proflaut.dms.repository.ProfMountPointRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class MountPointServiceImpl {

	@Autowired
	MountPointHelper helper;

	@Autowired
	ProfMountPointRepository mountPointRepository;

	@Autowired
	ProfUserPropertiesRepository profUserPropertiesRepository;

	@Autowired
	ProfMountFolderMappingRepository folderMappingRepository;

	@Autowired
	FolderRepository folderRepository;

	@Autowired
	ProfDocUploadRepository docUploadRepository;

	public ProfMountPointResponse saveMountPoint(ProfMountPointRequest pointRequest, String token) {
		ProfMountPointResponse mountPointResponse = new ProfMountPointResponse();
		ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
		Path directory = Paths.get(pointRequest.getPath());
		try {
			if (!entity.getToken().isEmpty() && Files.exists(directory) && Files.isDirectory(directory)) {
				ProfMountPointEntity mountPointEntity = helper.convertRequestToMountPointEntity(pointRequest, entity);
				mountPointRepository.save(mountPointEntity);
				mountPointResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				mountPointResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mountPointResponse;
	}

	public List<ProfMountPointOverallResponse> mountPoint() {
		List<ProfMountPointOverallResponse> pointOverallResponses = new ArrayList<>();
		try {
			List<ProfMountPointEntity> mountPointEntities = mountPointRepository.findAll();
			if (!mountPointEntities.isEmpty()) {
				for (ProfMountPointEntity mountPointEntity : mountPointEntities) {
					if (!mountPointEntity.getStatus().equalsIgnoreCase("I")) {
						ProfMountPointOverallResponse response = helper.convertToResponse(mountPointEntity);
						pointOverallResponses.add(response);
					}
				}
			} else {
				throw new CustomException("ProfMountPointEntity Is Null" + mountPointEntities);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pointOverallResponses;
	}

	public ProfMountPointOverallResponse findById(int id) {
		ProfMountPointOverallResponse pointOverallResponse = new ProfMountPointOverallResponse();

		ProfMountPointEntity mountPointEntities = mountPointRepository.findById(id);
		try {
			if (mountPointEntities.getPath() != null) {
				pointOverallResponse = helper.convertRequestToResponse(mountPointEntities);
			} else {
				throw new CustomException("Id Not Found " + mountPointEntities);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pointOverallResponse;
	}

	public ProfMountPointResponse saveMountPointMapping(ProfMountFolderMappingRequest folderMappingRequest,
			String token) {
		ProfMountPointResponse mountPointResponse = new ProfMountPointResponse();
		ProfUserPropertiesEntity entity = profUserPropertiesRepository.findByToken(token);
		try {
			if (!entity.getToken().isEmpty()) {
				List<ProfMountPointFolderMappingEntity> folderMappingEntity = helper
						.convertRequestToMappingEntity(folderMappingRequest, entity);
				folderMappingRepository.saveAll(folderMappingEntity);
				mountPointResponse.setStatus(DMSConstant.SUCCESS);

			} else {
				mountPointResponse.setStatus(DMSConstant.FAILURE);
				mountPointResponse.setStatus(DMSConstant.USERID_NOT_EXIST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mountPointResponse;
	}

	public List<FolderPathResponse> getAllNotAllocate(int id) {
		List<FolderPathResponse> folderPathResponses = new ArrayList<>();
		List<ProfMountPointFolderMappingEntity> folderMappingEntity = folderMappingRepository.findByMountPointId(id);
		try {
			if (folderMappingEntity == null) {
				List<FolderEntity> entity = folderRepository.findAll();
				for (FolderEntity folderEntity : entity) {
					FolderPathResponse pathResponse = helper.convertRequestToFolderResponse(folderEntity);
					folderPathResponses.add(pathResponse);
				}
			} else {
				List<FolderEntity> entity = folderRepository.findAllByIdNot(id);
				for (FolderEntity folderEntity : entity) {
					FolderPathResponse pathResponse = helper.convertRequestToFolderResponse(folderEntity);
					folderPathResponses.add(pathResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folderPathResponses;
	}

	public ProfMountPointResponse unAllocateFolders(int folderId, int mountId) {
		ProfMountPointResponse mountPointResponse = new ProfMountPointResponse();
		try {
			ProfMountPointFolderMappingEntity entity = folderMappingRepository.findByFolderIdAndMountPointId(folderId,
					mountId);
			List<ProfDocEntity> docEntity = docUploadRepository.findByFolderId(entity.getFolderId());
			for (ProfDocEntity profDocEntity : docEntity) {
				if (profDocEntity.getDocPath().isEmpty()) {
					folderMappingRepository.delete(entity);
					mountPointResponse.setStatus(DMSConstant.SUCCESS);
				} else {
					mountPointResponse.setStatus(DMSConstant.FAILURE);
					mountPointResponse.setErrorMessage("File Exist in the Folder");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mountPointResponse;
	}

	public List<FolderPathResponse> getAllAllocate(int mountId) {
		List<FolderPathResponse> folderPathResponses = new ArrayList<>();
		try {
			List<ProfMountPointFolderMappingEntity> mappingEntities = folderMappingRepository
					.findByMountPointId(mountId);
			if (!mappingEntities.isEmpty()) {
				for (ProfMountPointFolderMappingEntity profMountPointFolderMappingEntity : mappingEntities) {
					List<FolderEntity> entities = folderRepository
							.getById(profMountPointFolderMappingEntity.getFolderId());
					FolderPathResponse pathResponse = helper.convertrequestToAllocateResponse(entities);
					folderPathResponses.add(pathResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folderPathResponses;
	}

}
