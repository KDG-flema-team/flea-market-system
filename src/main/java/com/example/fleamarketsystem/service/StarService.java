package com.example.fleamarketsystem.service;

import com.example.fleamarketsystem.entity.Star;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.StarRepository;
import com.example.fleamarketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StarService {

    private final StarRepository starRepository;
    private final UserRepository userRepository;

    @Transactional
    public Star createStar(Long userId, Long targetUserId, Integer rating, String comment) {
        if (userId.equals(targetUserId)) {
            throw new RuntimeException("Cannot rate yourself");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        User targetUser = userRepository.findById(targetUserId)
            .orElseThrow(() -> new RuntimeException("Target user not found"));

        // Check if user already rated this target user
        starRepository.findByUserIdAndTargetUserId(userId, targetUserId)
            .ifPresent(s -> {
                throw new RuntimeException("You have already rated this user");
            });

        Star star = new Star();
        star.setUser(user);
        star.setTargetUser(targetUser);
        star.setRating(rating);
        star.setComment(comment);
        star.setCreatedAt(LocalDateTime.now());

        return starRepository.save(star);
    }

    @Transactional(readOnly = true)
    public List<Star> getStarsForUser(Long targetUserId) {
        return starRepository.findByTargetUserId(targetUserId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserRatingStats(Long targetUserId) {
        Double avgRating = starRepository.getAverageRatingForUser(targetUserId);
        Long count = starRepository.getStarCountForUser(targetUserId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", avgRating != null ? avgRating : 0.0);
        stats.put("totalStars", count);

        return stats;
    }

    @Transactional
    public Star updateStar(Long starId, Long userId, Integer rating, String comment) {
        Star star = starRepository.findById(starId)
            .orElseThrow(() -> new RuntimeException("Star not found"));

        if (!star.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own ratings");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        star.setRating(rating);
        star.setComment(comment);

        return starRepository.save(star);
    }

    @Transactional
    public void deleteStar(Long starId, Long userId) {
        Star star = starRepository.findById(starId)
            .orElseThrow(() -> new RuntimeException("Star not found"));

        if (!star.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own ratings");
        }

        starRepository.delete(star);
    }
}
