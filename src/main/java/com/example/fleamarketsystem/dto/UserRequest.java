package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
	
	@NotBlank(message="ユーザーネームは必須です")
	String name,
	
	@Email
	@NotBlank(message="メールアドレスは必須です")
	String email,
	
	@NotBlank(message="パスワードは必須です")
	String password
	
) {}
