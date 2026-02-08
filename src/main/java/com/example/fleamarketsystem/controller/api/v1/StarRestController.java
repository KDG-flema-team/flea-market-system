package com.example.fleamarketsystem.controller.api.v1;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.StarRequest;
import com.example.fleamarketsystem.dto.StarResponse;
import com.example.fleamarketsystem.dto.StarStatsResponse;
import com.example.fleamarketsystem.entity.Star;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.StarService;

@RestController
@RequestMapping("/api/v1/stars")
public class StarRestController {

    private final StarService starService;

    public StarRestController(StarService starService) {
        this.starService = starService;
    }

    @PostMapping
    public ResponseEntity<?> createStar(
            @LoginUser User loginUser,
            @Valid @RequestBody StarRequest request
    ) {
        

        try {
            Star saved = starService.createStar(
                    loginUser.getId(),
                    request.targetUserId(),
                    request.rating(),
                    request.comment()
            );
            return ResponseEntity.ok(toResponse(saved));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/{targetUserId}")
    public ResponseEntity<List<StarResponse>> getStarsForUser(@PathVariable Long targetUserId) {
        List<Star> stars = starService.getStarsForUser(targetUserId);
        List<StarResponse> responses = stars.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{targetUserId}/stats")
    public ResponseEntity<StarStatsResponse> getUserRatingStats(@PathVariable Long targetUserId) {
        Map<String, Object> stats = starService.getUserRatingStats(targetUserId);
        Double averageRating = (Double) stats.getOrDefault("averageRating", 0.0);
        Long totalStars = stats.get("totalStars") == null ? 0L : (Long) stats.get("totalStars");
        return ResponseEntity.ok(new StarStatsResponse(averageRating, totalStars));
    }

    @PutMapping("/{starId}")
    public ResponseEntity<?> updateStar(
    		@LoginUser User loginUser,
            @PathVariable Long starId,
            @Valid @RequestBody StarRequest request
    ) {

        try {
            Star updated = starService.updateStar(
                    starId,
                    loginUser.getId(),
                    request.rating(),
                    request.comment()
            );
            return ResponseEntity.ok(toResponse(updated));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{starId}")
    public ResponseEntity<?> deleteStar(
            @LoginUser User loginUser,
            @PathVariable Long starId
    ) {

        try {
            starService.deleteStar(starId, loginUser.getId());
            return ResponseEntity.ok(Map.of("message", "Star deleted successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    private StarResponse toResponse(Star star) {
        return new StarResponse(
                star.getId(),
                star.getUser().getId(),
                star.getUser().getName(),
                star.getTargetUser().getId(),
                star.getTargetUser().getName(),
                star.getRating(),
                star.getComment(),
                star.getCreatedAt()
        );
    }
}
