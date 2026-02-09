package com.example.fleamarketsystem.controller.api.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.security.AuthUser;
import com.example.fleamarketsystem.security.AuthUserResolver;
import com.example.fleamarketsystem.service.UserService;

@RestController
@RequestMapping("/api/v1/me")
public class MeRestController {

    private final UserService userService;
    private final AuthUserResolver authUserResolver;

    public MeRestController(UserService userService, AuthUserResolver authUserResolver) {
        this.userService = userService;
        this.authUserResolver = authUserResolver;
    }

    /**
     * 現在のユーザー情報を取得
     */
    @GetMapping
    public ResponseEntity<?> getMe(Authentication authentication) {
        User user = currentUser(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId().toString());
        response.put("name", user.getName());
        response.put("bio", user.getDescription());
        response.put("iconUrl", user.getProfileImageUrl()); // Auth0のプロフィール画像
        response.put("rank", user.getRank());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    /**
     * ユーザー情報を更新
     * 注: プロフィール画像はAuth0で管理されるため、ここでは更新しません
     */
    @PutMapping
    public ResponseEntity<?> updateMe(
            Authentication authentication,
            @RequestBody Map<String, String> body
    ) {
        User user = currentUser(authentication);

        String name = body.get("name");
        String bio = body.get("bio");
        // iconUrlはAuth0で管理されるため、ここでは更新しない

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }

        if (bio != null) {
            user.setDescription(bio);
        }

        userService.saveUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId().toString());
        response.put("name", user.getName());
        response.put("bio", user.getDescription());
        response.put("iconUrl", user.getProfileImageUrl()); // Auth0のプロフィール画像
        response.put("rank", user.getRank());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    private User currentUser(Authentication authentication) {
        AuthUser authUser = authUserResolver.resolve(authentication);
        return userService.getUserByEmail(authUser.email())
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
    }
}
