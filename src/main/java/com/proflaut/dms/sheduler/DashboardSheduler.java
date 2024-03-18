package com.proflaut.dms.sheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proflaut.dms.entity.DashboardDataEntity;
import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.entity.ProfDownloadHistoryEntity;
import com.proflaut.dms.entity.ProfUserInfoEntity;
import com.proflaut.dms.helper.FileHelper;
import com.proflaut.dms.repository.DashBoardRepository;
import com.proflaut.dms.repository.ProfDocUploadRepository;
import com.proflaut.dms.repository.ProfDownloadHistoryRepo;
import com.proflaut.dms.repository.ProfUserInfoRepository;
import com.proflaut.dms.service.impl.DashboardServiceImpl;

@Service
public class DashboardSheduler {

	ProfUserInfoRepository infoRepository;
	ProfDocUploadRepository docUploadRepository;
	DashBoardRepository boardRepository;
	FileHelper fileHelper;
	DashboardServiceImpl dashboardServiceImpl;
	ProfDownloadHistoryRepo downloadHistoryRepo;

	public  String formatCurrentDate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		return currentDateTime.format(formatter);
	}

	@Autowired
	public DashboardSheduler(ProfUserInfoRepository infoRepository, ProfDocUploadRepository docUploadRepository,
			DashBoardRepository boardRepository, FileHelper fileHelper, DashboardServiceImpl dashboardServiceImpl,
			ProfDownloadHistoryRepo downloadHistoryRepo) {
		this.infoRepository = infoRepository;
		this.docUploadRepository = docUploadRepository;
		this.boardRepository = boardRepository;
		this.fileHelper = fileHelper;
		this.dashboardServiceImpl = dashboardServiceImpl;
		this.downloadHistoryRepo = downloadHistoryRepo;
	}

//	@Scheduled(cron = "0 0 0 * * ?")
//  @Scheduled(fixedDelay = 10 * 1000)
	public void storeData() {
		List<DashboardDataEntity> dashboardDataEntities = new ArrayList<>();
		// Process upload data
		List<ProfUserInfoEntity> entities = infoRepository.findAll();
		for (ProfUserInfoEntity entity : entities) {
			String userName = entity.getUserName();
			List<ProfDocEntity> docEntities = docUploadRepository.findByCreatedByIgnoreCase(userName);
			List<ProfDownloadHistoryEntity> downloadHistoryEntities = downloadHistoryRepo.findByUserName(userName);
			if (!docEntities.isEmpty()) {
				DashboardDataEntity dashboardDataEntity = new DashboardDataEntity();
				dashboardDataEntity.setTotalUploads(String.valueOf(docEntities.size()));
				long userDocSize = fileHelper.getTotalFileSize(docEntities);
				String avgUploadSize = dashboardServiceImpl.calculateAverage(docEntities.size(), userDocSize);
				dashboardDataEntity.setAvgFileSize(avgUploadSize);
				String uploadSpeed = dashboardServiceImpl.calculateAverageExecutionTime(docEntities);
				dashboardDataEntity.setAvgUploadSpeed(uploadSpeed);
				dashboardDataEntity.setUserName(userName);
				dashboardDataEntity.setDate(formatCurrentDate());
				if (!downloadHistoryEntities.isEmpty()) {
					String averageDownloadSpeed = dashboardServiceImpl
							.calculateAverageDownloadSpeed(downloadHistoryEntities.size(), downloadHistoryEntities);
					dashboardDataEntity.setAvgDownloadSpeed(averageDownloadSpeed);
					dashboardDataEntity.setTotalDownloads(String.valueOf(downloadHistoryEntities.size()));
					dashboardDataEntity.setUserId(String.valueOf(entity.getUserId()));
					dashboardDataEntity.setDate(formatCurrentDate());
				}
				dashboardDataEntities.add(dashboardDataEntity);
			}
		}
		boardRepository.saveAll(dashboardDataEntities);
	}

}
