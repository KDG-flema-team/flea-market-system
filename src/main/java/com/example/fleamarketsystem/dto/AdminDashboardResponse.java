package com.example.fleamarketsystem.dto;

import java.util.List;

import com.example.fleamarketsystem.entity.AppOrder;
import com.example.fleamarketsystem.entity.Item;

public record AdminDashboardResponse(
		
    String role,
    List<Item> recentItems,
    List<AppOrder> recentOrders
    
) {}

