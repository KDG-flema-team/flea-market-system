package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record UserReponse(
	
	Long id,
	
	String name,
	
	String email,
	
	String role,
	
	Boolean enabled,
	
	Boolean banned,
	
	String banReason,
	
	LocalDateTime bannedAt
	
) {}
