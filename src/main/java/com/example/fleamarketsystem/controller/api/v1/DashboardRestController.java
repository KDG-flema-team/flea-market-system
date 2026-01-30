package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.AdminDashboardResponse;
import com.example.fleamarketsystem.dto.UserDashboardResponse;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ItemService;

@RestController
@RequestMapping("api/v1/dashboard")
public class DashboardRestController {
	
    private final ItemService itemService;
    private final AppOrderService appOrderService;

    public DashboardRestController(
    		
            ItemService itemService,
            AppOrderService appOrderService
            
            ) {
    	
        this.itemService = itemService;
        this.appOrderService = appOrderService;
        
    }
    
    @GetMapping
    public Object dashboard(
    		
    		@LoginUser User currentUser
    		
    		) {

        if ("ADMIN".equals(currentUser.getRole())) {
            return new AdminDashboardResponse(
                    "ADMIN",
                    itemService.getAllItems(),
                    appOrderService.getAllOrders()
            );
        } else {
            return new UserDashboardResponse(
                    "USER",
                    "/items"
            );
        }
    }

}
