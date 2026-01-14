package com.example.fleamarketsystem.controller.api;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.security.UserPrincipal;
import com.example.fleamarketsystem.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementApiController {

    private final UserManagementService userManagementService;

    @PostMapping("/{userId}/ban")
    public ResponseEntity<?> banUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            User user = userManagementService.banUser(userId, currentUser.getId(), reason);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to ban user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{userId}/unban")
    public ResponseEntity<?> unbanUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User user = userManagementService.unbanUser(userId, currentUser.getId());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to unban user");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{userId}/rank")
    public ResponseEntity<?> updateUserRank(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        try {
            String newRank = request.get("rank");
            User user = userManagementService.updateUserRank(userId, newRank);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update user rank");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
