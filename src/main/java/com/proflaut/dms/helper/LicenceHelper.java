package com.proflaut.dms.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.proflaut.dms.entity.ProfLicenseEntity;
import com.proflaut.dms.model.ProfLicenceResponse;
import com.proflaut.dms.statiClass.PasswordEncDecrypt;

@Component
public class LicenceHelper {

	private UUID uuid = UUID.randomUUID();

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
		String uuidString = uuid.toString().replace("-", "").substring(0, 8);
		ProfLicenseEntity entity = new ProfLicenseEntity();
		entity.setExpiryDate(decrypt.encrypt(formatCurrentDateTime()));
		entity.setLicenseKey("PROF" + formatCurrentDate() + uuidString);
		return entity;
	}

	public ProfLicenceResponse convertOverallResponse(ProfLicenseEntity profLicenseEntity, PasswordEncDecrypt decrypt) {
		ProfLicenceResponse licenceResponse=new ProfLicenceResponse();
		licenceResponse.setId(String.valueOf(profLicenseEntity.getId()));
		licenceResponse.setExpiryData(decrypt.decrypt(profLicenseEntity.getExpiryDate()));
		licenceResponse.setLicenceKey(profLicenseEntity.getLicenseKey());
		return licenceResponse;
	}

}
