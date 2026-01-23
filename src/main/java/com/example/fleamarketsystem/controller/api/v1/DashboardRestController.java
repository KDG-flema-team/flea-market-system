package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.AdminDashboardResponse;
import com.example.fleamarketsystem.dto.UserDashboardResponse;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.repository.UserRepository;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ItemService;

@RestController
@RequestMapping("api/v1/dashboard")
public class DashboardRestController {
	
	private final UserRepository userRepository;
    private final ItemService itemService;
    private final AppOrderService appOrderService;

    public DashboardRestController(
    		
            UserRepository userRepository,
            ItemService itemService,
            AppOrderService appOrderService
            
            ) {
    	
        this.userRepository = userRepository;
        this.itemService = itemService;
        this.appOrderService = appOrderService;
        
    }
    
    @GetMapping
    public Object dashboard(@AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = userRepository
                .findByEmailIgnoreCase(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

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
