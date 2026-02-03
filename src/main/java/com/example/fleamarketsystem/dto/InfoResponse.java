package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

import com.example.fleamarketsystem.entity.Info;

public record InfoResponse(
    Long id,
    String title,
    String content,
    String imageUrl,
    Boolean isImportant,
    LocalDateTime createAt
) {
    public static InfoResponse fromEntity(Info info) {
        return new InfoResponse(
            info.getId(),
            info.getTitle(),
            info.getContent(),
            info.getImageUrl(),
            info.getIsImportant(),
            info.getCreateAt()
        );
    }
}
