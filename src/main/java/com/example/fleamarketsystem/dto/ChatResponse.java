package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record ChatResponse(
	Long id,
	Long itemId,
	Long senderId,
	String senderName,
	String message,
	LocalDateTime createdAt
) {}
