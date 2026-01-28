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
import org.springframework.web.bind.annotation.RequestParam;


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
      @RequestParam(required = false, defaultValue = "false") Boolean onlyUnread
  ) {
      NotificationListResponse response = notificationService.getNotifications(
          user.getId(),
          startId,
          limit,
          onlyUnread
      );
      
      return ResponseEntity.ok(response);
  }
  
}
