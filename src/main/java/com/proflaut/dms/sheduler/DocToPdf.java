package com.proflaut.dms.sheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class DocToPdf {
	static String k = null;
	static OutputStream fileForPdf = null;

	public static void main(String[] args) {
		try {
			String fileName = "C:/Users/BILLPC01/Downloads/Loan-Agreement-LawRato3_output.docx";
			XWPFDocument docx = new XWPFDocument(new FileInputStream(fileName));
			// using XWPFWordExtractor Class
			XWPFWordExtractor we = new XWPFWordExtractor(docx);
			k = we.getText();
			fileForPdf = new FileOutputStream(
					new File("C:/Users/BILLPC01/Downloads/Loan-Agreement-LawRato3_output.pdf"));
			we.close();
			Document document = new Document();
			PdfWriter.getInstance(document, fileForPdf);

			document.open();

			document.add(new Paragraph(k));

			document.close();
			fileForPdf.close();
			System.out.println("SUCCESS");
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
}
