package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record AdminStatisticsResponse(
	
	LocalDate startDate,
	LocalDate endDate,
	BigDecimal totalSales,
	Map<String, Long> orderCountByStatus
	
) {}
