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

        Star saved = starRepository.save(star);
        
        // Update target user's rank based on average rating
        updateUserRankByAverageRating(targetUserId);
        
        return saved;
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

        Star updated = starRepository.save(star);
        
        // Update target user's rank based on average rating
        updateUserRankByAverageRating(star.getTargetUser().getId());
        
        return updated;
    }

    @Transactional
    public void deleteStar(Long starId, Long userId) {
        Star star = starRepository.findById(starId)
            .orElseThrow(() -> new RuntimeException("Star not found"));

        if (!star.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own ratings");
        }

        Long targetUserId = star.getTargetUser().getId();
        starRepository.delete(star);
        
        // Update target user's rank based on average rating
        updateUserRankByAverageRating(targetUserId);
    }

    /**
     * Update user's rank based on their average rating
     * bronze: 0.0 - 2.0
     * silver: 2.1 - 3.5
     * gold: 3.6 - 4.5
     * platinum: 4.6 - 5.0
     */
    @Transactional
    public void updateUserRankByAverageRating(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Double avgRating = starRepository.getAverageRatingForUser(userId);
        
        if (avgRating == null || avgRating == 0.0) {
            user.setRank("bronze");
        } else if (avgRating <= 2.0) {
            user.setRank("bronze");
        } else if (avgRating <= 3.5) {
            user.setRank("silver");
        } else if (avgRating <= 4.5) {
            user.setRank("gold");
        } else {
            user.setRank("platinum");
        }

        userRepository.save(user);
    }
}
