package com.example.fleamarketsystem.repository;

import com.example.fleamarketsystem.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByReportedUserId(Long reportedUserId);
    
    List<Report> findByReporterId(Long reporterId);
    
    List<Report> findByStatus(String status);
    
    Long countByReportedUserIdAndStatus(Long reportedUserId, String status);
}
