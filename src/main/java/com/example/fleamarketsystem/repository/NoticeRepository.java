package com.example.fleamarketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.fleamarketsystem.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

  List<Notice> findByUser_Id(Long userId);
  
  List<Notice> findByUser_IdAndIsRead(Long userId, Boolean isRead);
  
}
