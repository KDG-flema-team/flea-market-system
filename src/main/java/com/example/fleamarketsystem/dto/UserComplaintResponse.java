package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record UserComplaintResponse(
	
	Long id,
	
	Long reportedUserId,
	
	Long reporterUserId,
	
	String reason,
	
	LocalDateTime createdAt

) {}
