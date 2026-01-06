package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FavoriteResponse(
	Long id,
	Long userId,
	Long itemId,
	String itemName,
	BigDecimal price,
	LocalDateTime createdAt
) {}
