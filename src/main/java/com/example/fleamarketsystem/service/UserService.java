package com.example.fleamarketsystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.Jwt;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
	private String issuerUri;

	public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
		this.repo = repo;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getAllUsers() {
		return repo.findAll();
	}

	public Optional<User> getUserById(Long id) {
		return repo.findById(id);
	}

	public Optional<User> getUserByEmail(String email) {
		return repo.findByEmail(email);
	}

	@Transactional
	public User saveUser(User user) {
		return repo.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		repo.deleteById(id);
	}

	@Transactional
	public void banUser(Long userId) {
		User u = repo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		u.setBanned(true);
		repo.save(u);
	}

	@Transactional
	public void toggleUserEnabled(Long userId) {
		User u = repo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
		u.setEnabled(!u.isEnabled());
		repo.save(u);
	}

	@Transactional
	public User getOrCreateUserFromAuth0(Jwt jwt) {
		String auth0Id = jwt.getSubject();
		String email = jwt.getClaimAsString("email");
		String name = jwt.getClaimAsString("name");

		log.info("Auth0認証: auth0Id={}, email={}, name={}", auth0Id, email, name);

		// Auth0 IDでユーザーを検索
		Optional<User> existingUser = repo.findByAuth0Id(auth0Id);
		if (existingUser.isPresent()) {
			log.info("既存のAuth0ユーザーが見つかりました: userId={}", existingUser.get().getId());
			return existingUser.get();
		}

		// メールアドレスでも検索
		Optional<User> existingByEmail = repo.findByEmail(email);
		if (existingByEmail.isPresent()) {
			User user = existingByEmail.get();
			// 既存ユーザーにAuth0 IDを設定
			user.setAuth0Id(auth0Id);
			log.info("既存ユーザーにAuth0 IDを設定: userId={}, auth0Id={}", user.getId(), auth0Id);
			return repo.save(user);
		}

		// 新規ユーザーを作成
		log.info("新規Auth0ユーザーを作成: auth0Id={}, email={}", auth0Id, email);
		User newUser = new User();
		newUser.setAuth0Id(auth0Id);
		newUser.setEmail(email != null ? email : (auth0Id != null ? auth0Id + "@auth0.local" : "unknown@auth0.local"));
		String displayName = name;
		if (displayName == null) {
			if (email != null && !email.isEmpty() && email.contains("@")) {
				displayName = email.split("@")[0];
			} else {
				displayName = auth0Id != null ? auth0Id : "user";
			}
		}
		newUser.setName(displayName);
		newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // ランダムパスワード
		newUser.setRole("user");
		newUser.setRank("bronze");
		newUser.setEnabled(true);
		newUser.setBanned(false);

		return repo.save(newUser);
	}

	public User getorcreateUserFromEmail(Jwt jwt) {
		return getOrCreateUserFromAuth0(jwt);
	}

	public User getUserFromEmail(Jwt jwt) {
		String auth0Id = jwt.getSubject();

		// Auth0 IDでユーザーを検索
		Optional<User> existingUser = repo.findByAuth0Id(auth0Id);
		if (existingUser.isPresent()) {
			return existingUser.get();
		}

		// メールアドレスでも検索
		String email = jwt.getClaimAsString("email");
		Optional<User> existingByEmail = repo.findByEmail(email);
		if (existingByEmail.isPresent()) {
			User user = existingByEmail.get();
			// 既存ユーザーにAuth0 IDを設定
			user.setAuth0Id(auth0Id);
			return repo.save(user);
		}

		throw new IllegalArgumentException("User not found");
	}

	private Map<String, Object> parseJwtClaims(String jwtToken) {
		try {
			if (jwtToken == null) return Map.of();
			String[] parts = jwtToken.split("\\.");
			if (parts.length < 2) return Map.of();
			String payload = parts[1];
			// Base64 URL decode
			Base64.Decoder decoder = Base64.getUrlDecoder();
			byte[] decoded = decoder.decode(payload);
			String json = new String(decoded);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, Map.class);
		} catch (Exception e) {
			return Map.of();
		}
	}

	public User setAuth0Id(User user, String auth0Id) {
		user.setAuth0Id(auth0Id);
		return repo.save(user);
	}
}
