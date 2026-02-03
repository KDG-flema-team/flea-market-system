package com.example.fleamarketsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InfoRequest(
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
    String title,
    
    @Size(max = 255, message = "内容は255文字以内で入力してください")
    String content,
    
    @Size(max = 255, message = "画像URLは255文字以内で入力してください")
    String imageUrl,
    
    Boolean isImportant
) {}
