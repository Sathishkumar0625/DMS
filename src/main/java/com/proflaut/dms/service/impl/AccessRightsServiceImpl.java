package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfAccessRightsEntity;
import com.proflaut.dms.entity.ProfMetaDataEntity;
import com.proflaut.dms.helper.AccessRightsHelper;
import com.proflaut.dms.model.ProfAccessRightRequest;
import com.proflaut.dms.model.ProfAccessRightResponse;
import com.proflaut.dms.model.ProfOverallAccessRightsResponse;
import com.proflaut.dms.repository.ProfAccessRightRepository;
import com.proflaut.dms.repository.ProfMetaDataRepository;

@Service
public class AccessRightsServiceImpl {
	@Autowired
	ProfMetaDataRepository dataRepository;

	@Autowired
	AccessRightsHelper helper;

	@Autowired
	ProfAccessRightRepository accessRightRepository;

	@Autowired
	FileManagementServiceImpl serviceImpl;



	public ProfAccessRightResponse create(ProfAccessRightRequest accessRightRequest) {
		ProfAccessRightResponse accessRightResponse = new ProfAccessRightResponse();
		try {
			ProfAccessRightsEntity accessRights = helper.convertRequestToAccesEntity(accessRightRequest);
			accessRightRepository.save(accessRights);
			accessRightResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			accessRightResponse.setStatus(DMSConstant.FAILURE);
			accessRightResponse.setErrorMessage(e.getMessage());
		}
		return accessRightResponse;
	}

	public List<ProfOverallAccessRightsResponse> findAccess() {
		List<ProfOverallAccessRightsResponse> accessRightsResponses = new ArrayList<>();
		try {
			List<ProfAccessRightsEntity> accessRightsEntity = accessRightRepository.findAll();
			for (ProfAccessRightsEntity profAccessRightsEntity : accessRightsEntity) {
				ProfMetaDataEntity dataEntity = dataRepository.findById(Integer.parseInt(profAccessRightsEntity.getMetaId()));
				if (dataEntity != null) {
					ProfOverallAccessRightsResponse response = helper.convertToOverallResponse(profAccessRightsEntity,
							dataEntity);
					accessRightsResponses.add(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessRightsResponses;
	}

	public ProfOverallAccessRightsResponse findAccessById(String ids) {
		ProfOverallAccessRightsResponse accessRightsResponse = new ProfOverallAccessRightsResponse();
		try {
			int id = Integer.parseInt(ids);
			ProfAccessRightsEntity accessRightsEntity = accessRightRepository.findById(id);
			if (accessRightsEntity != null) {
				ProfMetaDataEntity dataEntity = dataRepository
						.findById(Integer.parseInt(accessRightsEntity.getMetaId()));
				if (dataEntity != null) {
					accessRightsResponse = helper.convertAccessEntityToResponse(accessRightsEntity, dataEntity, id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessRightsResponse;
	}
}
