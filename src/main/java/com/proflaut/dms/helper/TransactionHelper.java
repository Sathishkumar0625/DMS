package com.proflaut.dms.helper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proflaut.dms.constant.DMSConstant;
import com.proflaut.dms.entity.ProfActivitiesEntity;
import com.proflaut.dms.entity.ProfDmsHeader;
import com.proflaut.dms.entity.ProfDmsMainEntity;
import com.proflaut.dms.entity.ProfExecutionEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.exception.CustomException;
import com.proflaut.dms.model.AssociateConcerns;
import com.proflaut.dms.model.BankingCreditFacilities;
import com.proflaut.dms.model.FolderFO;
import com.proflaut.dms.model.InvoiceRequest;
import com.proflaut.dms.model.InvoiceResponse;
import com.proflaut.dms.model.ProfActivityRequest;
import com.proflaut.dms.model.ProfDmsMainRequest;
import com.proflaut.dms.model.ProfDmsMainReterive;
import com.proflaut.dms.model.ProfGetExecutionResponse;
import com.proflaut.dms.model.ProfUpdateDmsMainRequest;
import com.proflaut.dms.model.ProprietorParDirRequest;
import com.proflaut.dms.service.impl.FolderServiceImpl;
import com.proflaut.dms.util.AppConfiguration;

@Component
public class TransactionHelper {

	private final Random random = new Random();

	FolderServiceImpl folderServiceImpl;

	private final AppConfiguration appConfiguration;
	
	@Autowired
	public TransactionHelper(AppConfiguration appConfiguration,FolderServiceImpl folderServiceImpl) {
		this.appConfiguration = appConfiguration;
		this.folderServiceImpl=folderServiceImpl;
	}

	private String generateUniqueId() {
		return String.format("%04d", this.random.nextInt(999));
	}

	public ProfActivitiesEntity convertReqtoProfActEnti(ProfActivityRequest activityRequest,
			ProfUserInfoEntity entity) {
		ProfActivitiesEntity activitiesEntity = new ProfActivitiesEntity();
		activitiesEntity.setCreatedAt(LocalDateTime.now().toString());
		activitiesEntity.setGroupId(activityRequest.getGroupId());
		activitiesEntity.setKey(activityRequest.getKey());
		activitiesEntity.setProcessId(activityRequest.getProcessId());
		activitiesEntity.setTitle(activityRequest.getTitle());
		activitiesEntity.setStatus("A");
		activitiesEntity.setUserID(activityRequest.getUserID());
		activitiesEntity.setCreatedBy(entity.getUserName());
		return activitiesEntity;
	}

	public ProfDmsMainEntity convertMakerReqToMakerEntity(ProfDmsMainRequest mainRequest) throws CustomException {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setAccountNo(mainRequest.getAccountNo());
		mainEntity.setBranchcode(mainRequest.getBranchcode());
		mainEntity.setBranchName(mainRequest.getBranchName());
		mainEntity.setCustomerId(mainRequest.getCustomerId());
		mainEntity.setIfsc(mainRequest.getIfsc());
		mainEntity.setName(mainRequest.getName());
		mainEntity.setUserId(mainRequest.getUserId());
		mainEntity.setKey(mainRequest.getKey());
		String uniqueId = generateUniqueId();
		mainEntity.setProspectId("DMS_" + uniqueId);
		FolderFO folderFO = new FolderFO();
		folderFO.setProspectId("DMS_" + uniqueId);
		folderServiceImpl.saveFolder(folderFO, uniqueId);
		return mainEntity;
	}

	public ProfDmsMainReterive convertMainEntityToMainReterive(ProfDmsMainEntity dmsMainEntity) {
		ProfDmsMainReterive dmsMainReterive = new ProfDmsMainReterive();
		dmsMainReterive.setAccountNumber(dmsMainEntity.getAccountNo());
		dmsMainReterive.setBranchCode(dmsMainEntity.getBranchcode());
		dmsMainReterive.setBranchName(dmsMainEntity.getBranchName());
		dmsMainReterive.setCustomerId(dmsMainEntity.getCustomerId());
		dmsMainReterive.setIfsc(dmsMainEntity.getIfsc());
		dmsMainReterive.setName(dmsMainEntity.getName());
		dmsMainReterive.setProspectId(dmsMainEntity.getProspectId());
		return dmsMainReterive;
	}

	public ProfDmsMainEntity convertUpdateDmsReqToDmsEntity(ProfUpdateDmsMainRequest dmsMainRequest,
			ProfDmsMainEntity mainEntity) {
		mainEntity.setAccountNo(dmsMainRequest.getAccountNo());
		mainEntity.setBranchcode(dmsMainRequest.getBranchCode());
		mainEntity.setCustomerId(dmsMainRequest.getCustomerId());
		mainEntity.setIfsc(dmsMainRequest.getIfsc());
		mainEntity.setName(dmsMainRequest.getName());
		mainEntity.setBranchName(dmsMainRequest.getBranch());
		return mainEntity;
	}

	public ProfDmsHeader convertjsontoHeaderEntity(String jsonData) {
		ProfDmsHeader dmsHeader = new ProfDmsHeader();
		dmsHeader.setKey("maker");
		dmsHeader.setFields(convertToJsonString(jsonData));
		return dmsHeader;
	}

	private String convertToJsonString(String jsonData) {
		try {
			jsonData = jsonData.replace("\r", "").replace("\n", "");
			return jsonData;
		} catch (Exception e) {
			e.printStackTrace();
			return "Something Went Wrong";
		}
	}

	public ProfDmsMainEntity convertRequestToProfMain(int userId, String activityName,
			ProfExecutionEntity executionEntity) {
		ProfDmsMainEntity mainEntity = new ProfDmsMainEntity();
		mainEntity.setKey(activityName);
		mainEntity.setUserId(userId);
		mainEntity.setProspectId(executionEntity.getProspectId());
		return mainEntity;
	}

	public ProfExecutionEntity convertRequestToProfHeader(String activityName, ProfUserInfoEntity entity) {
		ProfExecutionEntity executionEntity = new ProfExecutionEntity();
		executionEntity.setActionBy(entity.getUserName());
		executionEntity.setActivityName(activityName);
		executionEntity.setEntryDate(LocalDateTime.now().toString());
		String uniqueId = generateUniqueId();
		executionEntity.setProspectId("DMS_" + uniqueId);
		executionEntity.setStatus("IN PROGRESS");
		return executionEntity;
	}

	public ProfGetExecutionResponse convertExecutionToGetExecution(ProfExecutionEntity profExecutionEntity) {
		ProfGetExecutionResponse executionResponse = new ProfGetExecutionResponse();
		executionResponse.setKey(profExecutionEntity.getActivityName());
		executionResponse.setProspectId(profExecutionEntity.getProspectId());
		return executionResponse;
	}

	public InvoiceResponse invoicegenerator(InvoiceRequest invoiceRequest) throws IOException {
		String inputFilename =appConfiguration.getInvoiceInputFileName() ;
		String outputfilename =appConfiguration.getInvoiceOutputFileName() ;


		try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(inputFilename)))) {

			doc.getTables().get(0).getRow(0).getCell(2).setText(" " + invoiceRequest.getApplicantName());
			doc.getTables().get(0).getRow(1).getCell(2).setText(invoiceRequest.getOfficeAddress());
			doc.getTables().get(0).getRow(2).getCell(2).setText(invoiceRequest.getAddressOfFactory());
			doc.getTables().get(0).getRow(3).getCell(2).setText(invoiceRequest.getCommunity());
			doc.getTables().get(0).getRow(4).getCell(2).setText(invoiceRequest.getMobileNo());
			doc.getTables().get(0).getRow(5).getCell(2).setText(invoiceRequest.geteMailAddress());
			doc.getTables().get(0).getRow(6).getCell(2).setText(invoiceRequest.getMobileNo());
			doc.getTables().get(0).getRow(7).getCell(2).setText(invoiceRequest.getPanCardNo());
			doc.getTables().get(0).getRow(9).getCell(2).setText(invoiceRequest.getDob());
			doc.getTables().get(0).getRow(10).getCell(2).setText(invoiceRequest.getState());
			doc.getTables().get(0).getRow(12).getCell(2).setText(invoiceRequest.getDistrict());
			int getIndex = 1;
			List<ProprietorParDirRequest> persons = invoiceRequest.getProprietorParDirRequests();
			if (persons != null && !persons.isEmpty()) {
				for (int i = 0; i < persons.size(); i++) {

					int rowOffset = 2 + i;
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(2).setText(persons.get(i).getName());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(3).setText(persons.get(i).getDateOfBirth());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(4)
							.setText(persons.get(i).getFatherSpouse());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(5)
							.setText(persons.get(i).getAcademicQualification());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(6).setText(persons.get(i).getMobileNo());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(7).setText(persons.get(i).getPanNo());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(8)
							.setText(persons.get(i).getResidentialAddress());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(9).setText(persons.get(i).getTelNoRes());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(10).setText(persons.get(i).getExperience());

				}
			}

			List<AssociateConcerns> associateConcerns = invoiceRequest.getAssociateConcerns();
			if (associateConcerns != null && !associateConcerns.isEmpty()) {
				for (int i = 0; i < associateConcerns.size(); i++) {

					int rowOffset = 10 + i;
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(2)
							.setText(associateConcerns.get(i).getNameOfAssociate());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(3)
							.setText(associateConcerns.get(i).getAddressOfAssoc());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(4)
							.setText(associateConcerns.get(i).getBankingWih());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(5)
							.setText(associateConcerns.get(i).getNatureOfAssociation());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(6)
							.setText(associateConcerns.get(i).getDirector());
				}
			}
			List<BankingCreditFacilities> creditFacilities = invoiceRequest.getBankingCreditFacilities();
			if (creditFacilities != null && !creditFacilities.isEmpty()) {
				for (int i = 0; i < creditFacilities.size(); i++) {
					int rowOffset = 17+i;
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(2)
							.setText(creditFacilities.get(i).getLimit());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(3)
							.setText(creditFacilities.get(i).getOutstanding());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(4)
							.setText(creditFacilities.get(i).getBankNameAndBranch());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(5)
							.setText(creditFacilities.get(i).getSecurities());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(6)
							.setText(creditFacilities.get(i).getInterestRate());
					doc.getTables().get(getIndex).getRow(rowOffset).getCell(7)
							.setText(creditFacilities.get(i).getRepaymentTerms());
				}
			}
			try (FileOutputStream out = new FileOutputStream(outputfilename)) {
				doc.write(out);
				InvoiceResponse invoiceResponse = new InvoiceResponse();
				invoiceResponse.setStatus(DMSConstant.SUCCESS);
				invoiceResponse.setFilePath(outputfilename);
				return invoiceResponse;
			} catch (IOException e) {
				e.printStackTrace();
				InvoiceResponse invoiceResponse = new InvoiceResponse();
				invoiceResponse.setStatus(DMSConstant.FAILURE);
				invoiceResponse.setErrorMessage("Failed to update invoice");
				return invoiceResponse;
			}

		}

	}
}
