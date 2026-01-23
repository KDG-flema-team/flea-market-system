package com.example.fleamarketsystem.service;

import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.fleamarketsystem.entity.AppOrder;
import com.example.fleamarketsystem.entity.Review;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.AppOrderRepository;
import com.example.fleamarketsystem.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppOrderRepository appOrderRepository;

    public ReviewService(
    		ReviewRepository reviewRepository,
    		AppOrderRepository appOrderRepository
    		) {
        this.reviewRepository = reviewRepository;
        this.appOrderRepository = appOrderRepository;
    }

    @Transactional
    public Review submitReview(
    		
    		Long orderId,
    		Long reviewerId,
    		int rating,
    		String comment
    		
    		) {
    	
        AppOrder order = appOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("注文が見つかりません"));
        
        
        
        // 購入者チェック
        if (!order.getBuyer().getId().equals(reviewerId)) {
            throw new IllegalStateException("この注文の購入者のみレビューできます");
        }

        // 二重レビュー防止
        if (reviewRepository.findByOrderId(orderId).isPresent()) {
            throw new IllegalStateException("この注文はすでにレビューされています");
        }

        Review review = new Review();
        review.setOrder(order);
        review.setReviewer(order.getBuyer());
        review.setSeller(order.getItem().getSeller());
        review.setItem(order.getItem());
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    public List<Review> getReviewsBySeller(User seller) {
        return reviewRepository.findBySeller(seller);
    }

    public OptionalDouble getAverageRatingForSeller(User seller) {
        return reviewRepository.findBySeller(seller).stream()
                .mapToInt(Review::getRating)
                .average();
    }

    public List<Review> getReviewsByReviewer(User reviewer) {
        return reviewRepository.findByReviewer(reviewer);
    }
}
