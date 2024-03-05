package com.proflaut.dms.service.impl;

import java.util.List;
import java.util.Map;

public interface DashboardService {
	List<Map<String, String>> getUserCounts();

	List<Map<String, String>> linearGraph(String token);

}
