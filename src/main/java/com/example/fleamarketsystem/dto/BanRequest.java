package com.example.fleamarketsystem.dto;

public record BanRequest(
	
	Boolean banned,
	String reason,
	Long adminId
	
) {}
