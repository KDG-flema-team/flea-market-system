package com.example.fleamarketsystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.fleamarketsystem.dto.OrdersSummaryResponse;
import com.example.fleamarketsystem.entity.AppOrder;
import com.example.fleamarketsystem.entity.User;

@Service
public class MyPageOrderService {
	
	private final AppOrderService appOrderService;

    public MyPageOrderService(
    		AppOrderService appOrderService
    		) {
        this.appOrderService = appOrderService;
    }
    
    public List<OrdersSummaryResponse> getOrderSummaries(User buyer) {
    	
    	return appOrderService.getOrdersByBuyer(buyer).stream()
    			.map(this::toSummary)
    			.toList();
    	
    }
    
    private OrdersSummaryResponse toSummary(AppOrder order) {
    	
    	return new OrdersSummaryResponse(
    			
    			order.getId(),
    			order.getItem().getId(),
    			order.getItem().getName(),
    			order.getPrice().intValue(),
    			
    			1, // 現状 quantity が無い前提
    			
    			order.getStatus(),
    			order.getReview() != null,
    			order.getCreatedAt()
    			
    		);
    	
    }

}
