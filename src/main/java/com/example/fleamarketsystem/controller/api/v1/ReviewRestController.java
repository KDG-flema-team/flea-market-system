package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.ReviewRequest;
import com.example.fleamarketsystem.dto.ReviewResponse;
import com.example.fleamarketsystem.entity.AppOrder;
import com.example.fleamarketsystem.entity.Review;
import com.example.fleamarketsystem.security.AuthUser;
import com.example.fleamarketsystem.security.AuthUserResolver;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ReviewService;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewRestController {
	
	private final ReviewService reviewService;
    private final AppOrderService appOrderService;
    private final AuthUserResolver authUserResolver;

    public ReviewRestController(
    		
            ReviewService reviewService,
            AppOrderService appOrderService,
            AuthUserResolver authUserResolver
            
            ) {
    	
        this.reviewService = reviewService;
        this.appOrderService = appOrderService;
        this.authUserResolver = authUserResolver;
        
    }
    
    @PostMapping
    public ResponseEntity<ReviewResponse> submitReview(
    		
    		Authentication authentication,
            @RequestBody ReviewRequest request
            
    ) {
    	
        AuthUser reviewer = authUserResolver.resolve(authentication);

        AppOrder order = appOrderService.getOrderById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("注文がありません"));

        Review review = reviewService.submitReview(
        		order.getId(),
        		reviewer.userId(),
        		request.rating(),
        		request.comment()
        		);
        
        ReviewResponse response = new ReviewResponse(
                review.getId(),
                order.getId(),
                order.getItem().getId(),
                order.getItem().getName(),
                reviewer.userId(),
                reviewer.email(),
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
