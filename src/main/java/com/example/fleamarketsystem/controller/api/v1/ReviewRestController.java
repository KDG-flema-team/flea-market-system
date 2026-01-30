package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.ReviewRequest;
import com.example.fleamarketsystem.dto.ReviewResponse;
import com.example.fleamarketsystem.entity.AppOrder;
import com.example.fleamarketsystem.entity.Review;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {
	
	private final ReviewService reviewService;
    private final AppOrderService appOrderService;

    public ReviewRestController(
    		
            ReviewService reviewService,
            AppOrderService appOrderService
            
            ) {
    	
        this.reviewService = reviewService;
        this.appOrderService = appOrderService;
        
    }
    
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
    		
    		@LoginUser User reviewer,
            @RequestBody ReviewRequest request
            
    ) {


        AppOrder order = appOrderService.getOrderById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("注文がありません"));

        Review review = reviewService.submitReview(
        		order.getId(),
        		reviewer.getId(),
        		request.rating(),
        		request.comment()
        		);
        
        ReviewResponse response = new ReviewResponse(
                review.getId(),
                order.getId(),
                order.getItem().getId(),
                order.getItem().getName(),
                reviewer.getId(),
                reviewer.getEmail(),
                order.getItem().getSeller().getId(),
                order.getItem().getSeller().getName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
        
        return ResponseEntity.ok(response);
    }
        
        /*
        return new ReviewResponse(
                null,
                order.getId(),
                order.getItem().getId(),
                order.getItem().getName(),
                reviewer.userId(),
                reviewer.email(),
                order.getItem().getSeller().getId(),
                order.getItem().getSeller().getName(),
                request.rating(),
                request.comment(),
                null
        );
        */

}
