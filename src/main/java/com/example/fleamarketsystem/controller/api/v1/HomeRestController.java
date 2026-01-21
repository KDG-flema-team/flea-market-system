package com.example.fleamarketsystem.controller.api.v1;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HomeRestController {
	
	@GetMapping("/home")
    public Map<String, String> home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 未ログイン
            return Map.of(
                    "role", "GUEST",
                    "redirect", "/items"
            );
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_ADMIN".equals(a));

        return Map.of(
                "role", isAdmin ? "ADMIN" : "USER",
                "redirect", isAdmin ? "/admin/users" : "/items"
        );
    }

}
