package com.example.fleamarketsystem.security;

import java.util.List;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository users;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// usernameParameter("email") にしているので username はメール
		User u = users.findByEmailIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		if (!u.isEnabled())
			throw new DisabledException("Account disabled");
		if (u.isBanned())
			throw new DisabledException("Account banned");

		return UserPrincipal.create(u);
	}

	@Transactional
	public UserDetails loadUserById(Long id) {
		User u = users.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

		if (!u.isEnabled())
			throw new DisabledException("Account disabled");
		if (u.isBanned())
			throw new DisabledException("Account banned");

		return UserPrincipal.create(u);
	}
}
