package com.example.fleamarketsystem.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository repo;
	private final PasswordEncoder passwordEncoder;

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
		newUser.setEmail(email);
		newUser.setName(name != null ? name : email.split("@")[0]); // 名前がない場合はメールアドレスから生成
		newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // ランダムパスワード
		newUser.setRole("USER");
		newUser.setRank("bronze");
		newUser.setEnabled(true);
		newUser.setBanned(false);

		return repo.save(newUser);
	}
}
