package com.proflaut.dms.helper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import com.proflaut.dms.model.ProfJobPackRequest;

@Component
public class JobPackHelper {

	public String convertToList(String inputFilename, String outputFilename, ProfJobPackRequest jobPackRequest) {
		try (FileInputStream fis = new FileInputStream(inputFilename);
				XWPFDocument document = new XWPFDocument(fis);
				FileOutputStream fos = new FileOutputStream(outputFilename)) {
//			insertImageAtFirstPage(document, "C:/Users/BILLPC01/Downloads/logo_landscap (1).jpg");
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
					/* -----> Insert Location <------ */
					insertLocation(paragraph, jobPackRequest);
					/* -----> Insert Day <------ */
					replaceDays(paragraph, jobPackRequest);
					/* -----> Insert Insert Date <------ */
					insertDate(paragraph, jobPackRequest);
					/* -----> Insert Insert Amount In Numbers <------ */
					insertAmountInNumbers(paragraph, jobPackRequest);
					/* -----> Insert Insert Amount In Words <------ */
					insertAmountInWords(paragraph, jobPackRequest);
					/* -----> Insert Lender Name <------ */
					insertLenderName(paragraph, jobPackRequest.getLenderName());
					/* -----> Insert Borrower Name <------ */
					insertBorrowerName(paragraph, jobPackRequest.getBorrowerName());
					/* -----> Insert Period Amount <------ */
					insertPeriodAmount(paragraph, jobPackRequest.getLoanPeriod());
					/* -----> Insert Loan Dates <------ */
					insertLoanDates(paragraph, jobPackRequest.getLoanPeriodFrom(), jobPackRequest.getLoanPeriodTo());
				}
			}

			// Save the modified document
			document.write(fos);

			return "Success";
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
	}

	/* -----> Insert Loan Dates <------ */
	private void insertLoanDates(XWPFParagraph paragraph, String fromDate, String terminatingDate) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null) {
				// Replace the placeholder for "from" date
				text = text.replace("from __/__/___", "from " + fromDate);
				// Replace the placeholder for "terminating" date
				text = text.replace("terminating on __/__/___", "terminating on " + terminatingDate);
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Period Amount <------ */
	private void insertPeriodAmount(XWPFParagraph paragraph, String periodAmount) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("period of ____")) {
				// Replace the placeholder with the period amount
				text = text.replace("period of ____", "period of " + periodAmount);
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Borrower Name <------ */
	private void insertBorrowerName(XWPFParagraph paragraph, String borrowerName) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("AND") && text.contains("____________________")) {
				// Replace the placeholder with the borrower name
				text = text.replaceFirst("____________________", borrowerName);
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Lender Name <------ */
	private void insertLenderName(XWPFParagraph paragraph, String lenderName) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("BETWEEN") && text.contains("__________________")) {
				// Replace the placeholder with the lender name
				text = text.replaceFirst("__________________", lenderName);
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Insert Amount In Numbers <------ */
	private void insertAmountInNumbers(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (XWPFRun run : runs) {
			String text = run.getText(0);
			if (text != null && text.contains("Rs.________/-")) {
				// Replace the placeholder with the actual amount
				text = text.replace("Rs.________/-", "Rs. " + jobPackRequest.getAmountInNumbers() + "/-");
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Insert Amount In Words <------ */
	private void insertAmountInWords(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("Rupees")) {
				// Extract the amount in numbers from jobPackRequest
				String amountInWords = jobPackRequest.getAmountInWords();
				// Split the text to insert the amount in numbers next to "Rs."
				String[] parts = text.split("Rupees");
				if (parts.length == 2) {
					// Construct the text with amount in numbers added next to "Rs."
					text = parts[0] + "Rupees  " + amountInWords + parts[1];
					run.setText(text, 0);
				}
			}
		}
	}

	/* -----> Insert DATE <------ */
	private void insertDate(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("day of_______")) {
				String currentDate = jobPackRequest.getCurrentDate();
				// Replace the placeholder with the current date
				text = text.replace("day of_______", "day of " + currentDate);
				run.setText(text, 0);
			}
		}
	}

	/* -----> Insert Day <------ */
	private void replaceDays(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		boolean dayInserted = false;
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && text.contains("this") && text.contains("day") && !dayInserted) {
				// Split the text by "this" and "day" to identify the position for inserting day
				String[] parts = text.split("this|day");
				if (parts.length == 3) {
					String day = jobPackRequest.getDay();
					// Insert the day between "this" and "day"
					text = parts[0] + "this " + day + " day" + parts[2];
					run.setText(text, 0);
					dayInserted = true;
				}
			}
		}
	}

	/* -----> Insert Location <------ */
	private void insertLocation(XWPFParagraph paragraph, ProfJobPackRequest jobPackRequest) {
		boolean locationInserted = false; // Flag to track if location has been inserted
		List<XWPFRun> runs = paragraph.getRuns();
		for (int i = 0; i < runs.size(); i++) {
			XWPFRun run = runs.get(i);
			String text = run.getText(0);
			if (text != null && (text.contains("at") && text.contains("this") && !locationInserted)) {
				// Split the text by "at" and "this" to identify the position for inserting
				// location
				String[] parts = text.split("at|this");
				if (parts.length == 3) {
					String location = jobPackRequest.getLocation();
					// Insert the location between "at" and "this"
					text = parts[0] + "at " + location + " this" + parts[2];
					run.setText(text, 0);
					locationInserted = true;
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

	/*
	 * private void insertImageAtFirstPage(XWPFDocument document, String imagePath)
	 * { // Get the first paragraph or create a new one if it doesn't exist
	 * XWPFParagraph firstParagraph; if (document.getParagraphs().isEmpty()) {
	 * firstParagraph = document.createParagraph(); } else { firstParagraph =
	 * document.getParagraphs().get(0); }
	 * 
	 * try { // Add the image to the paragraph XWPFRun run =
	 * firstParagraph.createRun(); run.addBreak(); // Ensure the image is inserted
	 * as a new line run.addPicture(new FileInputStream(imagePath),
	 * XWPFDocument.PICTURE_TYPE_PNG, imagePath, Units.toEMU(200),
	 * Units.toEMU(200)); // Adjust the width and height as needed } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */
}
