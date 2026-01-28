package com.example.fleamarketsystem.dto;

import java.util.List;

public record NotificationListResponse(
    List<NotificationResponse> notifications,
    Long nextStartId,
    Boolean hasMore,
    Integer unreadCount
) {
}
