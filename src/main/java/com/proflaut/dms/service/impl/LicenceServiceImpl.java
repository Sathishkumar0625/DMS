package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfLicenseEntity;
import com.proflaut.dms.helper.LicenceHelper;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.repository.ProfLicenseRepository;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;

@Service
public class LicenceServiceImpl {
	private static final Logger logger = LogManager.getLogger(LicenceServiceImpl.class);
	ProfLicenseRepository licenseRepository;
	LicenceHelper helper;
	
	
	@Autowired
	public LicenceServiceImpl(ProfLicenseRepository licenseRepository, LicenceHelper helper) {
		this.licenseRepository = licenseRepository;
		this.helper = helper;
	}

	public ProfLicenceResponse createLicence() {
		ProfLicenceResponse licenceResponse = new ProfLicenceResponse();
		try {
			PasswordEncDecrypt decrypt = new PasswordEncDecrypt();
			ProfLicenseEntity entity = helper.convertToLicenceEntity(decrypt);
			licenseRepository.save(entity);
			licenceResponse.setStatus(DMSConstant.SUCCESS);
		} catch (Exception e) {
			licenceResponse.setStatus(DMSConstant.FAILURE);
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return licenceResponse;
	}

	public List<ProfLicenceResponse> getLicenceKey() {
		List<ProfLicenceResponse> licenceResponses = new ArrayList<>();
		try {
			PasswordEncDecrypt decrypt = new PasswordEncDecrypt();
			List<ProfLicenseEntity> entity = licenseRepository.findAll();
			if (!entity.isEmpty()) {
				for (ProfLicenseEntity profLicenseEntity : entity) {
					ProfLicenceResponse licenceResponse = helper.convertOverallResponse(profLicenseEntity,decrypt);
					licenceResponses.add(licenceResponse);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return licenceResponses;
	}

}
