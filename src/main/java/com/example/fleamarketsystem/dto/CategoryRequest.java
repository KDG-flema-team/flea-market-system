package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
	@NotBlank(message="カテゴリ名は必須です")
	@Size(max = 100, message="カテゴリ名は100文字以内で入力してください")
	String name,
	
	Long parentId
	
) {}
