package com.example.fleamarketsystem.dto;

import com.example.fleamarketsystem.entity.User;

public record AdminUserResponse(
	
	Long id,
	String email,
	String name,
	boolean banned,
	String role,
	String rank
	
) {
	
	public static AdminUserResponse fromEntity(User user) {
		
		return new AdminUserResponse(
				
				user.getId(),
				user.getEmail(),
				user.getName(),
				user.isBanned(),
				user.getRole(),
				user.getRank()
				
		);
		
	}
	
}
