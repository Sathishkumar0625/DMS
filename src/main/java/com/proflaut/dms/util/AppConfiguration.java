package com.proflaut.dms.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
	@Value("${json.file.path}")
	private String jsonFilePath;
	@Value("${json.headerFile.path}")
	private String jsonHeaderFilePath;
	@Value("${invoice.input.fileName}")
	private String invoiceInputFileName;
	@Value("${invoice.output.fileName}")
	private String invoiceOutputFileName;

	public String getInvoiceInputFileName() {
		return invoiceInputFileName;
	}

	public String getInvoiceOutputFileName() {
		return invoiceOutputFileName;
	}

	public String getJsonFilePath() {
		return jsonFilePath;
	}

	public String getJsonHeaderFilePath() {
		return jsonHeaderFilePath;
	}

}
