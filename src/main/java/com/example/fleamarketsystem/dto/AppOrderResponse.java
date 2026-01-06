package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppOrderResponse (
	Long id,
	Long itemId,
	String itemName,
	Long buyerId,
	String buyerName,
	BigDecimal price,
	String status,
	LocalDateTime createdAt
) {}
