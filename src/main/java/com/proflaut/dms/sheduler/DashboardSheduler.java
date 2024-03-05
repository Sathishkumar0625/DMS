package com.proflaut.dms.sheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
//	@Scheduled(fixedDelay = 10 * 1000)
	public void storeData() {
		DashboardDataEntity dashboardDataEntity = null;
		List<ProfUserInfoEntity> entities = infoRepository.findAll();
		List<String> userNames = entities.stream().map(ProfUserInfoEntity::getUserName).collect(Collectors.toList());
		List<Integer> userIds = entities.stream().map(ProfUserInfoEntity::getUserId).collect(Collectors.toList());
		List<DashboardDataEntity> dashboardDataEntities = new ArrayList<>();
		for (String userName : userNames) {
			List<ProfDocEntity> docEntities = docUploadRepository.findByCreatedByIgnoreCase(userName);
			dashboardDataEntity = new DashboardDataEntity();
			dashboardDataEntity.setTotalUploads(String.valueOf(docEntities.size()));
			long userDocSize = fileHelper.getTotalFileSize(docEntities);
			String avgUploadSize = dashboardServiceImpl.calculateAverage(docEntities.size(), userDocSize);
			dashboardDataEntity.setAvgFileSize(avgUploadSize);
			String uploadSpeed = dashboardServiceImpl.calculateAverageExecutionTime(docEntities);
			dashboardDataEntity.setAvgUploadSpeed(uploadSpeed);
			dashboardDataEntities.add(dashboardDataEntity);
		}
		boardRepository.saveAll(dashboardDataEntities);
		for (Integer userid : userIds) {
			dashboardDataEntity = new DashboardDataEntity();
			List<ProfDownloadHistoryEntity> downloadHistoryEntities = downloadHistoryRepo.findByUserId(userid);
			String averageDownloadSpeed = dashboardServiceImpl
					.calculateAverageDownloadSpeed(downloadHistoryEntities.size(), downloadHistoryEntities);
			dashboardDataEntity.setAvgDownloadSpeed(Integer.parseInt(averageDownloadSpeed));
			dashboardDataEntity.setTotalDownloads(String.valueOf(downloadHistoryEntities.size()));
			dashboardDataEntities.add(dashboardDataEntity);
		}
		boardRepository.saveAll(dashboardDataEntities);
	}

}
