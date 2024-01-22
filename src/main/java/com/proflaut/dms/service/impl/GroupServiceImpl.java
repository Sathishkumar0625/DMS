package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfGroupInfoEntity;
import com.proflaut.dms.helper.GroupHelper;
import com.proflaut.dms.model.ProfGroupInfoRequest;
import com.proflaut.dms.model.ProfGroupInfoResponse;
import com.proflaut.dms.model.ProfOverallGroupInfoResponse;
import com.proflaut.dms.repository.ProfGroupInfoRepository;

@Service
public class GroupServiceImpl {

	@Autowired
	ProfGroupInfoRepository groupInfoRepository;

	@Autowired
	GroupHelper groupHelper;

	public String updateStatus(Integer id, ProfGroupInfoRequest groupInfoRequest) {
		try {
			Optional<ProfGroupInfoEntity> groupInfo = groupInfoRepository.findById(id);
			groupInfo.get().setStatus("I");
			groupInfoRepository.save(groupInfo.get());
			return DMSConstant.SUCCESS;
//			groupInfoRepository.findById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return DMSConstant.FAILURE;
		}

	}

	public ProfGroupInfoResponse createGroup(ProfGroupInfoRequest groupInfoRequest) {
		ProfGroupInfoResponse groupInfoResponse = new ProfGroupInfoResponse();
		try {
			ProfGroupInfoEntity groupInfoEntity = groupHelper.convertGroupInfoReqToGroupInfoEnt(groupInfoRequest);
			groupInfoRepository.save(groupInfoEntity);
			groupInfoResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponse;
	}

	public List<ProfOverallGroupInfoResponse> find() {
		List<ProfOverallGroupInfoResponse> groupInfoResponses = new ArrayList<>();
		try {
			List<ProfGroupInfoEntity> groupInfoEntities = groupInfoRepository.findAll();

			for (ProfGroupInfoEntity profGroupInfoEntity : groupInfoEntities) {
				if (!profGroupInfoEntity.getStatus().equalsIgnoreCase("I")) {
					ProfOverallGroupInfoResponse response = groupHelper.convertToResponse(profGroupInfoEntity);
					groupInfoResponses.add(response);
				} 
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupInfoResponses;
	}
}
