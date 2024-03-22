package com.proflaut.dms.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfJobpackTemplate;
import com.proflaut.dms.helper.JobPackHelper;
import com.proflaut.dms.model.ProfJobPackRequest;
import com.proflaut.dms.repository.ProfJobPackRepository;
import com.proflaut.dms.util.AppConfiguration;

@Service
public class JobPackServiceImpl {

	private static final Logger logger = LogManager.getLogger(JobPackServiceImpl.class);
	private final AppConfiguration appConfiguration;
	ProfJobPackRepository jobPackRepository;
	JobPackHelper jobPackHelper;

	@Autowired
	public JobPackServiceImpl(ProfJobPackRepository jobPackRepository, AppConfiguration appConfiguration,JobPackHelper jobPackHelper) {
		this.jobPackRepository = jobPackRepository;
		this.appConfiguration = appConfiguration;
		this.jobPackHelper=jobPackHelper;
	}

	public String getFields(ProfJobPackRequest jobPackRequest) {
		String fields = null;
		try {
			String inputFilename = appConfiguration.getInvoiceInputFileName();
			String outputFilename = appConfiguration.getInvoiceOutputFileName();
			fields = jobPackHelper.convertToList(inputFilename, outputFilename, jobPackRequest);
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return fields;
	}

	public List<Map<String, Object>> getAllTemp() {
		List<Map<String, Object>> response = new ArrayList<>();
		try {
			List<ProfJobpackTemplate> templates = jobPackRepository.findAll();
			for (ProfJobpackTemplate template : templates) {
				Map<String, Object> templateMap = new HashMap<>();
				templateMap.put("id", template.getId());
				templateMap.put("formName", template.getFormName());
				String[] fieldNames = template.getFieldName().split("\\r?\\n");
				templateMap.put("fieldName", fieldNames);
				response.add(templateMap);
			}
		} catch (Exception e) {
			logger.error(DMSConstant.PRINTSTACKTRACE, e.getMessage(), e);
		}
		return response;
	}

}
