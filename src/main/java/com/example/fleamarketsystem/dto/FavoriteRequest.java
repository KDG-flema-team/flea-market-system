package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.NotNull;

public record FavoriteRequest(
	@NotNull(message="商品IDは必須です")
	Long itemId
) {}
