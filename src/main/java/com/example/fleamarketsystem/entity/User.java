// src/main/java/com/example/fleamarketsystem/entity/User.java
package com.example.fleamarketsystem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "auth0_id", unique = true)
	private String auth0Id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role; // "USER" or "ADMIN"

	@Column(nullable = false)
	private String rank = "bronze"; // "bronze", "silver", "gold", "platinum"

	@Column(nullable = false)
	private boolean enabled = true;

	@Column(nullable = false)
	private boolean banned = false;

	@Column(name = "ban_reason")
	private String banReason;

	@Column(name = "banned_at")
	private LocalDateTime bannedAt;

	@Column(name = "banned_by_admin_id")
	private Integer bannedByAdminId;
	
	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "profile_image_url", length = 500)
	private String profileImageUrl;

	public boolean hasRole(String role) {
	    return this.role.equals(role);
	}

}
