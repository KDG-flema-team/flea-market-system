package com.example.fleamarketsystem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Info {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String title;

  @Column(length = 255)
  private String content;

  @Column(name = "image_url", length = 255)
  private String imageUrl;

  @Column(name = "is_important", nullable = false)
  private Boolean isImportant = false;

  @Column(name = "create_at")
  private LocalDateTime createAt;
}
