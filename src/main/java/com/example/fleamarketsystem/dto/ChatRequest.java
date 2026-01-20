package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(

	@NotBlank(message="メッセージを入力してください")
	@Size(max=1000, message="メッセージは1000文字以内で入力してください")
	String message
	
) {}