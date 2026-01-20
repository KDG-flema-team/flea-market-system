package com.example.fleamarketsystem.controller.api;

import com.example.fleamarketsystem.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserApiController {

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(401).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("sub", jwt.getSubject());
            response.put("email", jwt.getClaimAsString("email"));
            response.put("name", jwt.getClaimAsString("name"));
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
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        try {
            if (jwt == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not authenticated");
                return ResponseEntity.status(401).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("sub", jwt.getSubject());
            response.put("email", jwt.getClaimAsString("email"));
            response.put("name", jwt.getClaimAsString("name"));
            response.put("aud", jwt.getAudience());
            response.put("iss", jwt.getIssuer());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user profile");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}