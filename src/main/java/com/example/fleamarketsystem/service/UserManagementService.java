package com.example.fleamarketsystem.service;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserRepository userRepository;

    @Transactional
    public User banUser(Long userId, Long adminId, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Only admins can ban users");
        }

        if (user.isBanned()) {
            throw new RuntimeException("User is already banned");
        }

        user.setBanned(true);
        user.setBanReason(reason);
        user.setBannedAt(LocalDateTime.now());
        user.setBannedByAdminId(adminId.intValue());

        return userRepository.save(user);
    }

    @Transactional
    public User unbanUser(Long userId, Long adminId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Only admins can unban users");
        }

        if (!user.isBanned()) {
            throw new RuntimeException("User is not banned");
        }

        user.setBanned(false);
        user.setBanReason(null);
        user.setBannedAt(null);
        user.setBannedByAdminId(null);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserRank(Long userId, String newRank) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!isValidRank(newRank)) {
            throw new RuntimeException("Invalid rank. Must be one of: bronze, silver, gold, platinum");
        }

        user.setRank(newRank);
        return userRepository.save(user);
    }

    private boolean isValidRank(String rank) {
        return "bronze".equals(rank) || "silver".equals(rank) || 
               "gold".equals(rank) || "platinum".equals(rank);
    }
}
