package com.proflaut.dms.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfLanguageConverterEntity;
import com.proflaut.dms.entity.ProfLicenseEntity;
import com.proflaut.dms.helper.LicenceHelper;
import com.proflaut.dms.model.ProfLanguageConverterRequest;
import com.proflaut.dms.model.ProfLanguageConverterResponse;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.repository.LangRepository;
import com.proflaut.dms.repository.ProfLicenseRepository;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;

@Service
public class LicenceServiceImpl {
	private static final Logger logger = LogManager.getLogger(LicenceServiceImpl.class);
	ProfLicenseRepository licenseRepository;
	LicenceHelper helper;
	
	@Autowired
	LangRepository langRepository;
	
	
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
			List<ProfLicenseEntity> entity = licenseRepository.findAll();
			if (!entity.isEmpty()) {
				for (ProfLicenseEntity profLicenseEntity : entity) {
					ProfLicenceResponse licenceResponse = helper.convertOverallResponse(profLicenseEntity);
					licenceResponses.add(licenceResponse);
				}
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return licenceResponses;
	}
	
	public ProfLanguageConverterResponse converter(ProfLanguageConverterRequest converterRequest) {
		ProfLanguageConverterResponse converterResponse = new ProfLanguageConverterResponse();
		try {
			String urlStr = "https://script.google.com/macros/s/AKfycbwxnpKuF8teqZAr-2lfWL6uBjQ1R6nLjX0Ks6lcISQdrk_K5RcqEXRhm1whVTFh6daB/exec"
					+ "?q=" + URLEncoder.encode(converterRequest.getTextValue(), "UTF-8") + "&target=" + "en";
			URL url = new URL(urlStr);
			StringBuilder response = new StringBuilder();
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			ProfLanguageConverterEntity converterEntity=helper.convertRequestToEntity(converterRequest,response.toString());
			langRepository.save(converterEntity);
			converterResponse.setConvertedText(response.toString());
			converterResponse.setStatus("SUCCESS");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return converterResponse;
	}

}
