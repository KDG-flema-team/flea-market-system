package com.example.fleamarketsystem.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

@Component
public class AuthUserResolver {
	
	private final UserRepository userRepository;
	
	public AuthUserResolver(UserRepository userRepository) {
		
		this.userRepository = userRepository;
		
	}
	
	public AuthUser resolve(Authentication authentication) {
		
		if (authentication == null || !authentication.isAuthenticated()) {
			
			throw new RuntimeException("認証されていません。");
			
		}
		
		Object principal = authentication.getPrincipal();
		
		if (principal instanceof UserDetails userDetails) {
			
			String email = userDetails.getUsername();
			
			User user = userRepository.findByEmailIgnoreCase(email)
					.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
			
			return new AuthUser(
					user.getId(),
					user.getEmail(),
					user.getRole()
					);
		}
		
		/* 将来的にAuth0を導入 */
		
		/*
		 if (principal instanceof Jwt jwt) {
			 
			 String auth0UserId = jwt.getSubject();
			 
			 User user = userRepository.findByAuth0UserId(Auth0UserId)
					 .orElseThrow(() -> new RuntimeException("認証されていません"));
			 
			 return new AuthUser(
					 user.getId(),
					 user.getEmail(),
					 user.getRole()
					 );
			 
		 }
		 */
		 
		 throw new RuntimeException("Unsupported Principal");
		
	}

}
