package com.example.fleamarketsystem.controller.api;

import com.example.fleamarketsystem.entity.Report;
import com.example.fleamarketsystem.security.UserPrincipal;
import com.example.fleamarketsystem.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportApiController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> createReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, Object> request) {
        try {
            Long reportedUserId = Long.parseLong(request.get("reportedUserId").toString());
            String reason = request.get("reason").toString();

            Report report = reportService.createReport(currentUser.getId(), reportedUserId, reason);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create report");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllReports() {
        try {
            List<Report> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReportsByStatus(@PathVariable String status) {
        try {
            List<Report> reports = reportService.getReportsByStatus(status);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getReportsForUser(@PathVariable Long userId) {
        try {
            List<Report> reports = reportService.getReportsForUser(userId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{reportId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> reviewReport(
            @PathVariable Long reportId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            Report report = reportService.reviewReport(reportId, currentUser.getId(), newStatus);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to review report");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/user/{userId}/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingReportCount(@PathVariable Long userId) {
        try {
            Long count = reportService.getPendingReportCountForUser(userId);
            Map<String, Long> response = new HashMap<>();
            response.put("pendingCount", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get pending report count");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
