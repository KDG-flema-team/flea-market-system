package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;


public record AppOrderRequest(
	@NotNull(message="商品IDは必須です")
	Long itemId,
	
	@NotNull(message="値段は必須です")
	BigDecimal price
) {}
