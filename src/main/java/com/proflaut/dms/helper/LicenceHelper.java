package com.proflaut.dms.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;
import com.proflaut.dms.entity.ProfLanguageConverterEntity;
import com.proflaut.dms.entity.ProfLicenseEntity;
import com.proflaut.dms.model.ProfJobPackRequest;
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

	public String convertToList(String inputFilename, String outputFilename, ProfJobPackRequest jobPackRequest) {
		try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(inputFilename)));
				FileOutputStream out = new FileOutputStream(outputFilename)) {
			boolean foundAgreement = false;
			for (XWPFParagraph paragraph : doc.getParagraphs()) {
				String paragraphText = paragraph.getText();
				if (paragraphText.startsWith("LOAN AGREEMENT BETWEEN")) {
					foundAgreement = true;
					continue;
				}
				if (foundAgreement) {
					// Insert borrower's name after "LOAN AGREEMENT BETWEEN"
					insertAfterLine(paragraph, jobPackRequest.getBorrowerName());
					foundAgreement = false;
					continue; // Skip processing this paragraph
				}
				if (paragraphText.startsWith("AND")) {
					// Insert lender's name after "AND"
					insertAfterWord(paragraph, jobPackRequest.getLenderName(), "AND");
					continue; // Skip processing this paragraph
				}
				
				insertLocationIfMatch(paragraph, jobPackRequest.getLocation());
			}
			doc.write(out); // Write changes back to the output file
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
		return "Success";
	}

	// Method to insert text after a specific line
	private void insertAfterLine(XWPFParagraph paragraph, String textToInsert) {
		XWPFRun run = paragraph.createRun();
		run.setText(textToInsert);
	}

	// Method to insert text after a specific word
	private void insertAfterWord(XWPFParagraph paragraph, String textToInsert, String targetWord) {
		// Find the index of the target word in the paragraph text
		int index = paragraph.getText().indexOf(targetWord);
		if (index != -1) {
			// Insert text after the target word
			XWPFRun run = paragraph.createRun();
			run.setText(paragraph.getText().substring(0, index + targetWord.length()) + " " + textToInsert
					+ paragraph.getText().substring(index + targetWord.length()));
		}
	}

	private void insertLocationIfMatch(XWPFParagraph paragraph, String location) {
		Pattern pattern = Pattern.compile("\\bat\\s+\\w+\\s+this\\b", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(paragraph.getText());
		if (matcher.find()) {
			// Insert location after the matched word
			int index = matcher.start() + matcher.group().length();
			String updatedText = paragraph.getText().substring(0, index) + " " + location
					+ paragraph.getText().substring(index);
			paragraph.removeRun(0); // Remove existing run
			XWPFRun run = paragraph.createRun();
			run.setText(updatedText);
		}
	}

}
