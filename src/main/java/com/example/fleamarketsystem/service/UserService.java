package com.example.fleamarketsystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

@Service
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
	public User getOrCreateUserFromAuth0(String auth0Id, String email, String name) {
		// Auth0 IDでユーザーを検索
		Optional<User> existingUser = repo.findByAuth0Id(auth0Id);
		if (existingUser.isPresent()) {
			return existingUser.get();
		}

		// メールアドレスでも検索
		Optional<User> existingByEmail = repo.findByEmail(email);
		if (existingByEmail.isPresent()) {
			User user = existingByEmail.get();
			// 既存ユーザーにAuth0 IDを設定
			user.setAuth0Id(auth0Id);
			return repo.save(user);
		}

		// 新規ユーザーを作成
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
		newUser.setRole("USER");
		newUser.setRank("bronze");
		newUser.setEnabled(true);
		newUser.setBanned(false);

		return repo.save(newUser);
	}

	public User getorcreateUserFromEmail(String auth0Id, String email, String name, String token) {
		// Auth0 IDでユーザーを検索
		Optional<User> existingUser = repo.findByAuth0Id(auth0Id);
		if (existingUser.isPresent()) {
			return existingUser.get();
		}

		// メールアドレスでも検索
		Optional<User> existingByEmail = repo.findByEmail(email);
		if (existingByEmail.isPresent()) {
			User user = existingByEmail.get();
			// 既存ユーザーにAuth0 IDを設定
			user.setAuth0Id(auth0Id);
			return repo.save(user);
		}

		// 新規ユーザーを作成
		User newUser = new User();
		newUser.setAuth0Id(auth0Id);
		newUser.setEmail(email != null ? email : (auth0Id != null ? auth0Id + "@auth0.local" : "unknown@auth0.local"));
		String displayName2 = name;
		if (displayName2 == null) {
			if (email != null && !email.isEmpty() && email.contains("@")) {
				displayName2 = email.split("@")[0];
			} else {
				displayName2 = auth0Id != null ? auth0Id : "user";
			}
		}
		newUser.setName(displayName2);
		newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // ランダムパスワード
		newUser.setRole("USER");
		newUser.setRank("bronze");
		newUser.setEnabled(true);
		newUser.setBanned(false);

		return repo.save(newUser);
	}

	public User getUserFromEmail(String auth0Id, String email, String name, String token) {
		// Auth0 IDでユーザーを検索
		Optional<User> existingUser = repo.findByAuth0Id(auth0Id);
		if (existingUser.isPresent()) {
			return existingUser.get();
		}

		// メールアドレスでも検索
		Optional<User> existingByEmail = repo.findByEmail(email);
		if (existingByEmail.isPresent()) {
			User user = existingByEmail.get();
			// 既存ユーザーにAuth0 IDを設定
			user.setAuth0Id(auth0Id);
			return repo.save(user);
		}

		throw new IllegalArgumentException("User not found");
	}

	public User setAuth0Id(User user, String auth0Id) {
		user.setAuth0Id(auth0Id);
		return repo.save(user);
	}
}
