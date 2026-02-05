package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.fleamarketsystem.entity.Info;
import com.example.fleamarketsystem.entity.Notice;

public record NotificationResponse(
    Long id,
    String message,
    LocalDateTime createdAt,
    Boolean isRead,
    Boolean isImportant,
    String linkUrl,
    String category,
    String title,
    String body,
    String date,
    String type,
    String relatedItemId
) {
    // InfoとNoticeのIDスペースを分離するためのオフセット
    public static final long INFO_ID_OFFSET = 1_000_000L;

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
    
    public static NotificationResponse fromNotice(Notice notice) {
        String title = notice.getTitle() != null ? notice.getTitle() : "通知";
        String body = notice.getContent() != null ? notice.getContent() : "";
        String message = body.isEmpty() ? title : body;
        String formattedDate = notice.getCreatedAt() != null 
            ? notice.getCreatedAt().format(DATE_FORMATTER) 
            : "";
        
        return new NotificationResponse(
            notice.getId(),
            message,
            notice.getCreatedAt(),
            notice.getIsRead(),
            false,
            null,
            "notice",
            title,
            body,
            formattedDate,
            "transaction",
            null // Notices don't have related items currently
        );
    }
    
    public static NotificationResponse fromInfo(Info info) {
        String title = info.getTitle() != null ? info.getTitle() : "お知らせ";
        String body = info.getContent() != null ? info.getContent() : "";
        String message = body.isEmpty() ? title : body;
        String formattedDate = info.getCreateAt() != null
            ? info.getCreateAt().format(DATE_FORMATTER)
            : "";

        return new NotificationResponse(
            info.getId() + INFO_ID_OFFSET, // InfoのIDにオフセットを加算してNoticeとの衝突を回避
            message,
            info.getCreateAt(),
            true, // Info is always considered read
            info.getIsImportant(),
            info.getImageUrl(),
            "info",
            title,
            body,
            formattedDate,
            "operation",
            null
        );
    }
}
