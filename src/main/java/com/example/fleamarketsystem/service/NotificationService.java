package com.example.fleamarketsystem.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarketsystem.dto.NotificationListResponse;
import com.example.fleamarketsystem.dto.NotificationResponse;
import com.example.fleamarketsystem.entity.Info;
import com.example.fleamarketsystem.entity.Notice;
import com.example.fleamarketsystem.repository.InfoRepository;
import com.example.fleamarketsystem.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NoticeRepository noticeRepository;
    private final InfoRepository infoRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId, Long startId, Integer limit, Boolean onlyUnread) {
        // Set default limit
        if (limit == null || limit < 1) {
            limit = 20;
        }
        if (limit > 50) {
            limit = 50;
        }

        // Get user-specific notices
        List<Notice> notices = noticeRepository.findByUser_Id(userId);
        
        // Get important info for all users
        List<Info> importantInfos = infoRepository.findByisImportantTrue();

        // Combine and convert to NotificationResponse
        List<NotificationResponse> allNotifications = new ArrayList<>();
        
        // Add notices
        for (Notice notice : notices) {
            allNotifications.add(NotificationResponse.fromNotice(notice));
        }
        
        // Add important info
        for (Info info : importantInfos) {
            allNotifications.add(NotificationResponse.fromInfo(info));
        }

        // Sort by createdAt descending (newest first)
        allNotifications.sort(Comparator.comparing(NotificationResponse::createdAt).reversed());

        // Filter by startId if provided
        if (startId != null) {
            allNotifications = allNotifications.stream()
                .filter(n -> n.id() < startId)
                .collect(Collectors.toList());
        }

        // Filter by read status if onlyUnread is true
        if (onlyUnread != null && onlyUnread) {
            allNotifications = allNotifications.stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());
        }

        // Calculate unread count
        int unreadCount = (int) notices.stream()
            .filter(n -> !n.getIsRead())
            .count();

        // Paginate
        boolean hasMore = allNotifications.size() > limit;
        List<NotificationResponse> paginatedNotifications = allNotifications.stream()
            .limit(limit)
            .collect(Collectors.toList());

        // Determine nextStartId
        Long nextStartId = null;
        if (hasMore && !paginatedNotifications.isEmpty()) {
            nextStartId = paginatedNotifications.get(paginatedNotifications.size() - 1).id();
        }

        return new NotificationListResponse(
            paginatedNotifications,
            nextStartId,
            hasMore,
            unreadCount
        );
    }

    @Transactional
    public void markAsRead(Long noticeId, Long userId) {
        Notice notice = noticeRepository.findById(noticeId)
            .orElseThrow(() -> new RuntimeException("Notice not found"));
        
        if (!notice.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        notice.setIsRead(true);
        noticeRepository.save(notice);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        List<Notice> unreadNotices = noticeRepository.findByUser_IdAndIsRead(userId, false);
        
        for (Notice notice : unreadNotices) {
            notice.setIsRead(true);
        }
        
        noticeRepository.saveAll(unreadNotices);
        return unreadNotices.size();
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(Long userId) {
        List<Notice> notices = noticeRepository.findByUser_Id(userId);
        return (int) notices.stream()
            .filter(n -> !n.getIsRead())
            .count();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long notificationId, Long userId) {
        // Try to find as Notice first
        var notice = noticeRepository.findById(notificationId);
        if (notice.isPresent()) {
            Notice n = notice.get();
            // Check if the notice belongs to the user
            if (!n.getUser().getId().equals(userId)) {
                throw new RuntimeException("Unauthorized");
            }
            return NotificationResponse.fromNotice(n);
        }
        
        // Try to find as Info
        var info = infoRepository.findById(notificationId);
        if (info.isPresent()) {
            return NotificationResponse.fromInfo(info.get());
        }
        
        throw new RuntimeException("Notification not found");
    }
}
