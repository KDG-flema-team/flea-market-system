package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
	
	Long id,
	
	Long orderId,
	
	Long itemId,
	String itemName,
	
	Long reviewerId,
	String reviewerName,
	
	Long sellerId,
	String sellerName,
	
	Integer rating,
	
	String comment,
	
	LocalDateTime createdAt
	
) {}
