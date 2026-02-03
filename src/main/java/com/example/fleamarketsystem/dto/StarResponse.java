package com.example.fleamarketsystem.dto;

import java.time.LocalDateTime;

public record StarResponse(
        Long id,
        Long userId,
        String userName,
        Long targetUserId,
        String targetUserName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
