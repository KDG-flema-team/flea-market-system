package com.example.fleamarketsystem.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;
import com.example.fleamarketsystem.service.UserService;

@Component
public class AuthUserResolver {

	private final UserRepository userRepository;
	private final UserService userService;

	public AuthUserResolver(UserRepository userRepository, UserService userService) {

		this.userRepository = userRepository;
		this.userService = userService;

	}
	
	public AuthUser resolve(Authentication authentication) {
		
		if (authentication == null || !authentication.isAuthenticated()) {
			
			throw new AuthenticationCredentialsNotFoundException("認証が必要です");
			
		}
		
		Object principal = authentication.getPrincipal();
		
		/* フォームログイン */
		
		if (principal instanceof UserDetails userDetails) {
			
			String email = userDetails.getUsername();
			
			User user = userRepository.findByEmailIgnoreCase(email)
					.orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
			
			return new AuthUser(
					user.getId(),
					user.getEmail(),
					user.getRole()
					);
		}
		
		/* Auth0 JWT認証 */

		if (principal instanceof Jwt jwt) {

			// ユーザーが存在しない場合は自動的に作成
			User user = userService.getOrCreateUserFromAuth0(jwt);

			return new AuthUser(
					user.getId(),
					user.getEmail(),
					user.getRole()
					);

		}

		throw new AuthenticationCredentialsNotFoundException("未対応の認証方式です");
		
	}

}
