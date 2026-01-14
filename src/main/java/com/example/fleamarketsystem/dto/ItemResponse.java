package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemResponse(
	
	Long id,
	
	String name,
	
	String description,
	
	BigDecimal price,
	
	String status,
	
	String imageUrl,
	
	Long categoryId,
	String categoryName,
	
	Long sellerId,
	String sellerName,
	
	LocalDateTime createdAt
	
) {}
