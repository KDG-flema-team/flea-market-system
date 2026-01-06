package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
	
	@NotNull
	Long orderId,
	
	@NotNull
	Long itemId,
	
	@NotNull
	Long sellerId,
	
	@NotNull
	@Min(1)
	@Max(5)
	Integer rating,
	
	@Size(max=1000)
	String comment
		
) {}
