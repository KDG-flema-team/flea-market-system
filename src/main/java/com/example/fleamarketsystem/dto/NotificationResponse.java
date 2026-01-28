package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

import com.example.fleamarketsystem.entity.Info;
import com.example.fleamarketsystem.entity.Notice;

public record NotificationResponse(
    Long id,
    String message,
    LocalDateTime createdAt,
    Boolean isRead,
    Boolean isImportant,
    String linkUrl,
    String category
) {
    
    public static NotificationResponse fromNotice(Notice notice) {
        return new NotificationResponse(
            notice.getId(),
            notice.getContent() != null ? notice.getContent() : notice.getTitle(),
            notice.getCreatedAt(),
            notice.getIsRead(),
            false,
            null,
            "notice"
        );
    }
    
    public static NotificationResponse fromInfo(Info info) {
        return new NotificationResponse(
            info.getId(),
            info.getContent() != null ? info.getContent() : info.getTitle(),
            info.getCreateAt(),
            true, // Info is always considered read
            info.getIsImportant(),
            info.getImageUrl(),
            "info"
        );
    }
}
