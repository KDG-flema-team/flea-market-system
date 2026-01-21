package com.example.fleamarketsystem.controller.api;

import com.example.fleamarketsystem.security.UserPrincipal;
import com.example.fleamarketsystem.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.entity.User;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserApiController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(401).body(error);
            }
            
            // Auth0ユーザーをデータベースに自動登録
            String auth0Id = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            
            User user = userService.getOrCreateUserFromAuth0(auth0Id, email, name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sub", jwt.getSubject());
            response.put("email", email);
            response.put("name", name);
            response.put("userId", user.getId());
            response.put("claims", jwt.getClaims());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get current user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my-page")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Jwt jwt, @LoginUser User user) {
        try {
            if (jwt == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(401).body(error);
            }
            
            // Auth0ユーザーをデータベースに自動登録
            String auth0Id = jwt.getSubject();
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            
            User dbUser = userService.getOrCreateUserFromAuth0(auth0Id, email, name);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sub", jwt.getSubject());
            response.put("email", email);
            response.put("name", name);
            response.put("aud", jwt.getAudience());
            response.put("iss", jwt.getIssuer());
            response.put("user", dbUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user profile");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}