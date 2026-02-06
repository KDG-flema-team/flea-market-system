package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.NotificationListResponse;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.fleamarketsystem.dto.NotificationResponse;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<NotificationListResponse> getNotifications(
            @LoginUser User user,
            @RequestParam(required = false) Long startId,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyUnread) {
        if (user == null) {
            return ResponseEntity.ok(new NotificationListResponse(new ArrayList<>(), null, false, 0));
        }
        NotificationListResponse response = notificationService.getNotifications(
                user.getId(),
                startId,
                limit,
                onlyUnread);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(
            @LoginUser User user,
            @PathVariable Long id) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).build();
            }
            NotificationResponse response = notificationService.getNotificationById(id, user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Notification not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(403).build();
            }
            throw e;
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @LoginUser User user,
            @PathVariable Long id) {
        try {
            if (user == null) {
                return ResponseEntity.status(401).build();
            }
            notificationService.markAsRead(id, user.getId());
            NotificationResponse response = notificationService.getNotificationById(id, user.getId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Notification not found") || e.getMessage().equals("Notice not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(403).build();
            }
            throw e;
        }
    }

    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@LoginUser User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        int updatedCount = notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("updatedCount", updatedCount));
    }

}
