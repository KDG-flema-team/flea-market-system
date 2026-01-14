package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserComplaintRequest(
	
	@NotNull(message="通報対象ユーザーIDは必須です")
	Long reportedUserId,
	
	@NotBlank(message="通報理由は必須です")
	String reason
	
) {}
