package com.example.fleamarketsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fleamarketsystem.entity.Info;

public interface InfoRepository extends JpaRepository<Info, Long> {
  List<Info> findByisImportantTrue();
}
