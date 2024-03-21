package com.proflaut.dms.helper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
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
		try {
			// Load the Word document
			FileInputStream fis = new FileInputStream(inputFilename);
			XWPFDocument document = new XWPFDocument(fis);

			// Manipulate the document
			boolean foundLoanAgreement = false;
			for (XWPFParagraph paragraph : document.getParagraphs()) {
				String text = paragraph.getText();
				// Check if the paragraph contains the target phrases
				if (text != null) {
					if (text.contains("LOAN AGREEMENT BETWEEN")) {
						// Add lender name directly under "LOAN AGREEMENT BETWEEN"
						addNameUnderPhrase(paragraph, jobPackRequest.getLenderName(), "LOAN AGREEMENT BETWEEN");
						foundLoanAgreement = true;
					} else if (text.contains("AND") && foundLoanAgreement) {
						// Add borrower name directly under "AND"
						addNameUnderPhrase(paragraph, jobPackRequest.getBorrowerName(), "AND");
						foundLoanAgreement = false; // Reset flag after adding name
					}
					// Replace placeholders for location, day, and date
					replacePlaceholders(paragraph, jobPackRequest);
				}
			}

			// Save the modified document
			FileOutputStream fos = new FileOutputStream(outputFilename);
			document.write(fos);

			// Close streams
			fis.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
		return "Success";
	}

	private void replacePlaceholders(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null) {
				// Replace location placeholder
				if (text.contains("at") && text.contains("this")) {
					String location = jobPackRequest.getLocation();
					text = text.replace("_____", " " + location);
					run.setText(text, 0);
					break;
				}
			}
		}
	}

	private static void addNameUnderPhrase(XWPFParagraph paragraph, String name, String phrase) {
		int index = paragraph.getText().indexOf(phrase);
		if (index >= 0) {
			// Clear the existing text in the paragraph
			for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
				paragraph.removeRun(i);
			}

			// Add the phrase
			XWPFRun run = paragraph.createRun();
			run.setText(phrase);
			run.addCarriageReturn(); // Add a new line

			// Add the name
			run = paragraph.createRun();
			run.setText(name);
			run.addCarriageReturn(); // Add a new line
		}
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
