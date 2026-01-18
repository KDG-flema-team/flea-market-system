package com.example.fleamarketsystem.security;

public record AuthUser(
	
	Long userId,
	String email,
	String role
	
) {}
