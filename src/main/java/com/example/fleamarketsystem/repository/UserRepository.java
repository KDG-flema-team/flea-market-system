// src/main/java/com/example/fleamarketsystem/repository/UserRepository.java
package com.example.fleamarketsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.fleamarketsystem.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailIgnoreCase(String email);

	Optional<User> findByEmail(String email);

	Optional<User> findByName(String name);

	@Query("SELECT u FROM User u WHERE u.auth0Id = :auth0Id")
	Optional<User> findByAuth0Id(@Param("auth0Id") String auth0Id);

	boolean existsByEmail(String email);

	// キャストは CAST(... AS double precision) にして、:userId との衝突を回避
	@Query(value = """
			SELECT CAST(COALESCE(AVG(r.rating), 0) AS double precision)
			  FROM review r
			 WHERE r.seller_id   = :userId
			    OR r.reviewer_id = :userId
			""", nativeQuery = true)
	Double averageRatingForUser(@Param("userId") Long userId);
}
