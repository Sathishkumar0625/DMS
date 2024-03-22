package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import com.proflaut.dms.entity.ProfLanguageConverterEntity;
import com.proflaut.dms.entity.ProfLicenseEntity;
import com.proflaut.dms.model.ProfLanguageConverterRequest;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.staticlass.PasswordEncDecrypt;

@Component
public class LicenceHelper {

	public String formatCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		return currentDateTime.format(formatter);
	}

	public String formatCurrentDateTime() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd-MM-yyyy HH:mm ");
		return currentDateTime.format(formatter);
	}

	public ProfLicenseEntity convertToLicenceEntity(PasswordEncDecrypt decrypt) {
		ProfLicenseEntity entity = new ProfLicenseEntity();
		entity.setExpiryDate(decrypt.encrypt(formatCurrentDateTime()));
		entity.setLicenseKey(decrypt.encrypt("PROF" + formatCurrentDate() + "U2000C200T"));
		return entity;
	}

	public ProfLicenceResponse convertOverallResponse(ProfLicenseEntity profLicenseEntity) {
		ProfLicenceResponse licenceResponse = new ProfLicenceResponse();
		licenceResponse.setId(String.valueOf(profLicenseEntity.getId()));
		licenceResponse.setExpiryData((profLicenseEntity.getExpiryDate()));
		licenceResponse.setLicenceKey(profLicenseEntity.getLicenseKey());
		return licenceResponse;
	}

	public ProfLanguageConverterEntity convertRequestToEntity(ProfLanguageConverterRequest converterRequest,
			String newLang) {
		ProfLanguageConverterEntity converterEntity = new ProfLanguageConverterEntity();
		converterEntity.setTextId(converterRequest.getTextId());
		converterEntity.setConvertedText(newLang);
		converterEntity.setOriginalText(converterRequest.getTextValue());
		return converterEntity;
	}
}
