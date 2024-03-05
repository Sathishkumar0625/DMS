package com.proflaut.dms.service.impl;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.FolderEntity;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.entity.ProfUserPropertiesEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.helper.FolderHelper;
import com.proflaut.dms.model.FileResponse;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.FolderRetreiveResponse;
import com.proflaut.dms.model.Folders;
import com.proflaut.dms.model.ProfFolderRetrieveResponse;
import com.proflaut.dms.model.ProfMountFolderMappingRequest;
import com.proflaut.dms.repository.FolderRepository;
import com.proflaut.dms.repository.ProfAccessGroupMappingRepository;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfAccessUserMappingRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;
import com.proflaut.dms.repository.ProfUserGroupMappingRepository;
import com.proflaut.dms.repository.ProfUserPropertiesRepository;

@Service
public class FolderServiceImpl {
	
	private static final Logger logger = LogManager.getLogger(FolderServiceImpl.class);
	
	@Value("${create.folderlocation}")
	private String folderLocation;

	FolderRepository folderRepo;

	FolderHelper helper;

	ProfDocUploadRepository docUploadRepository;

	ProfUserPropertiesRepository profUserPropertiesRepository;

	ProfMetaDataRepository dataRepository;

	ProfUserGroupMappingRepository groupMappingRepository;

	ProfAccessGroupMappingRepository accessGroupMappingRepository;

	ProfAccessUserMappingRepository accessUserMappingRepository;

	ProfAccessRightRepository accessRightRepository;

	MountPointServiceImpl pointServiceImpl;
	
	
	@Autowired
	public FolderServiceImpl(FolderRepository folderRepo, FolderHelper helper,
			ProfDocUploadRepository docUploadRepository, ProfUserPropertiesRepository profUserPropertiesRepository,
			ProfMetaDataRepository dataRepository, ProfUserGroupMappingRepository groupMappingRepository,
			ProfAccessGroupMappingRepository accessGroupMappingRepository,
			ProfAccessUserMappingRepository accessUserMappingRepository,
			ProfAccessRightRepository accessRightRepository, MountPointServiceImpl pointServiceImpl) {
		this.folderRepo = folderRepo;
		this.helper = helper;
		this.docUploadRepository = docUploadRepository;
		this.profUserPropertiesRepository = profUserPropertiesRepository;
		this.dataRepository = dataRepository;
		this.groupMappingRepository = groupMappingRepository;
		this.accessGroupMappingRepository = accessGroupMappingRepository;
		this.accessUserMappingRepository = accessUserMappingRepository;
		this.accessRightRepository = accessRightRepository;
		this.pointServiceImpl = pointServiceImpl;
	}

	public FileResponse saveFolder(FolderFO folderFO, String token) throws CustomException {
		FileResponse fileResponse = new FileResponse();
		try {
			ProfMetaDataEntity dataEntity = dataRepository.findById(Integer.parseInt(folderFO.getMetaDataId()));
			ProfUserPropertiesEntity propertiesEntity = profUserPropertiesRepository.findByToken(token);
			if (dataEntity != null && propertiesEntity != null) {
				String folderPath = folderLocation + folderFO.getFolderName();
				String path=Paths.get(folderPath).toString();
				File folder = new File(path);

				if (folder.exists()) {
					fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
					fileResponse.setStatus(DMSConstant.FAILURE);
					return fileResponse;
				}

				FolderEntity folderEnt = helper.convertFOtoBO(folderFO, propertiesEntity);
				FolderEntity folderRespEnt = folderRepo.save(folderEnt);
				List<Integer> digits = convertToArrayList(folderEnt.getId());
				ProfMountFolderMappingRequest folderMappingRequest = new ProfMountFolderMappingRequest();
				folderMappingRequest.setMountPointId(folderFO.getMountId());
				folderMappingRequest.setFolderId(digits);
				pointServiceImpl.saveMountPointMapping(folderMappingRequest, token);
				fileResponse.setId(folderRespEnt.getId());
				fileResponse.setFolderPath(folderRespEnt.getFolderPath());
				fileResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				fileResponse.setErrorMessage(DMSConstant.FOLDER_ALREADY_EXIST);
				fileResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			throw new CustomException(e.getMessage());
		}
		return fileResponse;
	}

	private List<Integer> convertToArrayList(Integer id) {
		List<Integer> digits = new ArrayList<>();
		while (id > 0) {
			digits.add(0, id % 10);
			id /= 10;
		}
		return digits;
	}

	public FileResponse retriveFile(int id) {
		FileResponse fileResponse = new FileResponse();
		try {
			FolderEntity folderEntity = folderRepo.findById(id);
			if (folderEntity != null) {
				fileResponse.setStatus(DMSConstant.SUCCESS);
				fileResponse.setProspectId(String.valueOf(id));
				fileResponse.setFolderPath(folderEntity.getFolderPath());
			} else {
				fileResponse.setStatus(DMSConstant.FAILURE);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return fileResponse;
	}

	public Folders retreive(int id) {
		Folders folders = new Folders();
		try {
			FolderEntity folderEntity = folderRepo.findById(id);
			if (folderEntity != null) {
				List<ProfDocEntity> docEntity = docUploadRepository.findByFolderId(folderEntity.getId());
				folders = helper.convertFolderEntityToFolderFo(folderEntity, docEntity);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);

		}
		return folders;

	}

	public FolderRetreiveResponse getAllFolders(String token) {
		FolderRetreiveResponse folderRetreiveResponse = new FolderRetreiveResponse();
		try {
			ProfUserPropertiesEntity propertiesEntity = profUserPropertiesRepository.findByToken(token);
			if (propertiesEntity != null) {
				int userId = propertiesEntity.getUserId();
				List<Folders> folders = helper.convertToGetAllFolders(userId);
				folderRetreiveResponse.setFolder(folders);
				folderRetreiveResponse.setStatus(DMSConstant.SUCCESS);
			} else {
				throw new CustomException("Token Not Found" + token);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
			folderRetreiveResponse.setStatus(DMSConstant.FAILURE);
		}
		return folderRetreiveResponse;
	}

	public ProfFolderRetrieveResponse fetchByParentId(int parentFolderID) {
		ProfFolderRetrieveResponse folders = new ProfFolderRetrieveResponse();
		try {
			List<FolderEntity> entity = folderRepo.findByParentFolderID(parentFolderID);
			if (entity != null) {
				List<String> metaIds = entity.stream().map(FolderEntity::getMetaId).collect(Collectors.toList());
				List<ProfAccessRightsEntity> accessRights = accessRightRepository.findByMetaIdIn(metaIds);
				folders = helper.convertFolderEntityToFolderRetrieveResponse(entity, accessRights);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return folders;
	}

}
