package com.proflaut.dms.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proflaut.dms.entity.ProfDocEntity;
import com.proflaut.dms.repository.ProfDocUploadRepository;

@RestController
@RequestMapping("sample")
public class WeeklyReaport {
	
	@Autowired
  ProfDocUploadRepository profDocUploadRepository;
	
	
	@GetMapping("/get")
    public void method() {
        // Instantiate WeeklyReaport with a properly initialized profDocUploadRepository
        WeeklyReaport weeklyReaport = new WeeklyReaport();

        // Call the generateWeeklyReport method to get the weekly report
        Map<String, Long> weeklyReport = weeklyReaport.generateWeeklyReport("rakesh");

        // Print the weekly report
        for (Map.Entry<String, Long> entry : weeklyReport.entrySet()) {
            System.out.println("Week Range: " + entry.getKey() + ", File Size: " + entry.getValue() + " bytes");
        }
    }

    public Map<String, Long> generateWeeklyReport(String userId) {
        // Map to store weekly file uploads
        Map<String, Long> weeklyReport = new HashMap<>();

        try {
            // Retrieve documents uploaded by the user
            List<ProfDocEntity> userDocs = profDocUploadRepository.getById(1);

            if (!userDocs.isEmpty()) {
                // Parse the string into LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(userDocs.get(0).getCreatedBy(), formatter);

                // Group documents by week of the year and sum file sizes for each week
                Map<Integer, Long> weeklyFileSizes = userDocs.stream()
                        .collect(Collectors.groupingBy(doc -> dateTime.get(WeekFields.ISO.weekOfWeekBasedYear()),
                                Collectors.summingLong(doc -> Long.parseLong(doc.getFileSize()))));

                // Convert week number to week start date and populate weekly report
                weeklyFileSizes.forEach((weekNumber, fileSize) -> {
                    LocalDate weekStartDate = LocalDate.now().with(WeekFields.ISO.weekOfWeekBasedYear(), weekNumber)
                            .with(DayOfWeek.MONDAY);
                    String weekRange = weekStartDate.toString() + " - " + weekStartDate.plusDays(6).toString();
                    weeklyReport.put(weekRange, fileSize);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return weeklyReport;
    }
}
