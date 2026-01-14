package com.example.fleamarketsystem.controller.api;

import com.example.fleamarketsystem.entity.Star;
import com.example.fleamarketsystem.security.UserPrincipal;
import com.example.fleamarketsystem.service.StarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stars")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class StarApiController {

    private final StarService starService;

    @PostMapping
    public ResponseEntity<?> createStar(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, Object> request) {
        try {
            Long targetUserId = Long.parseLong(request.get("targetUserId").toString());
            Integer rating = Integer.parseInt(request.get("rating").toString());
            String comment = request.get("comment") != null ? request.get("comment").toString() : null;

            Star star = starService.createStar(currentUser.getId(), targetUserId, rating, comment);
            return ResponseEntity.ok(star);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create star");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getStarsForUser(@PathVariable Long userId) {
        try {
            List<Star> stars = starService.getStarsForUser(userId);
            return ResponseEntity.ok(stars);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get stars");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserRatingStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = starService.getUserRatingStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get rating stats");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{starId}")
    public ResponseEntity<?> updateStar(
            @PathVariable Long starId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, Object> request) {
        try {
            Integer rating = Integer.parseInt(request.get("rating").toString());
            String comment = request.get("comment") != null ? request.get("comment").toString() : null;

            Star star = starService.updateStar(starId, currentUser.getId(), rating, comment);
            return ResponseEntity.ok(star);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update star");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{starId}")
    public ResponseEntity<?> deleteStar(
            @PathVariable Long starId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            starService.deleteStar(starId, currentUser.getId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Star deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete star");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
