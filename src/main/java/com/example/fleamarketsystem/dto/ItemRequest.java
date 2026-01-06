package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record ItemRequest(
		
	@NotBlank
	String name,
	
	String description,
	
	@NotNull
	@DecimalMin(value="0.0", inclusive=false)
	BigDecimal price,
	
	@NotNull
	Long categoryId,
	
	String status,
	
	String imageUrl
	
) {}