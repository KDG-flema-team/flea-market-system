package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record OrdersSummaryResponse(
	
	Long orderId,
	
	Long itemId,
	String itemName,
	
	int price,
	int quantity,
	
	String status,
	
	Boolean reviewed,
	
	LocalDateTime orderedAt
	
) {}
