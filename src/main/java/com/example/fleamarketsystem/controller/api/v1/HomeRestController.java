package com.example.fleamarketsystem.controller.api.v1;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.entity.User;

@RestController
@RequestMapping("/api/v1")
public class HomeRestController {
	
	@GetMapping("/home")
    public Map<String, String> home(
    		
    		@LoginUser User currentUser
    		
    ) {
        if (currentUser == null) {
            // 未ログイン
            return Map.of(
                    "role", "GUEST",
                    "redirect", "/items"
            );
        }

        boolean isAdmin = "ADMIN".equals(currentUser.getRole());

        return Map.of(
                "role", isAdmin ? "ADMIN" : "USER",
                "redirect", isAdmin ? "/admin/users" : "/items"
        );
    }

}
