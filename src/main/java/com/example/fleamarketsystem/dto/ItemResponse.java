package com.example.fleamarketsystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.fleamarketsystem.entity.Item;

public record ItemResponse(

	Long id,

	String name,

	String description,

	BigDecimal price,

	String status,

	List<String> imageUrls,

	Long categoryId,
	String categoryName,

	Long sellerId,
	String sellerName,

	LocalDateTime createdAt

) {
	
	public static ItemResponse from(Item item) {

        return new ItemResponse(

            item.getId(),
            item.getName(),
            item.getDescription(),
            item.getPrice(),
            item.getStatus(),
            item.getImageUrls(),

            item.getCategory().getId(),
            item.getCategory().getName(),

            item.getSeller().getId(),
            item.getSeller().getName(),

            item.getCreatedAt()

        );

    }
	
}
