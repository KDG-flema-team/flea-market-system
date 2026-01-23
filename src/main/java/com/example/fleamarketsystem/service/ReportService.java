package com.example.fleamarketsystem.service;

import com.example.fleamarketsystem.entity.Report;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.ReportRepository;
import com.example.fleamarketsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public Report createReport(Long reporterId, Long reportedUserId, String reason) {
        if (reporterId.equals(reportedUserId)) {
            throw new RuntimeException("Cannot report yourself");
        }

        User reporter = userRepository.findById(reporterId)
            .orElseThrow(() -> new RuntimeException("Reporter not found"));
        User reportedUser = userRepository.findById(reportedUserId)
            .orElseThrow(() -> new RuntimeException("Reported user not found"));

        Report report = new Report();
        report.setReporter(reporter);
        report.setReportedUser(reportedUser);
        report.setReason(reason);
        report.setStatus("PENDING");
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Report> getReportsForUser(Long reportedUserId) {
        return reportRepository.findByReportedUserId(reportedUserId);
    }

    @Transactional
    public Report reviewReport(Long reportId, Long adminId, String newStatus) {
        Report report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));

        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("Only admins can review reports");
        }

        report.setStatus(newStatus);
        report.setReviewedAt(LocalDateTime.now());
        report.setReviewedByAdmin(admin);

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Long getPendingReportCountForUser(Long reportedUserId) {
        return reportRepository.countByReportedUserIdAndStatus(reportedUserId, "PENDING");
    }
}
