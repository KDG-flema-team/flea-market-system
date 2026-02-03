package com.example.fleamarketsystem.controller.api.v1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.OrdersSummaryResponse;
import com.example.fleamarketsystem.dto.UserReponse;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.security.AuthUser;
import com.example.fleamarketsystem.security.AuthUserResolver;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.FavoriteService;
import com.example.fleamarketsystem.service.ItemService;
import com.example.fleamarketsystem.service.MyPageOrderService;
import com.example.fleamarketsystem.service.ReviewService;
import com.example.fleamarketsystem.service.UserService;

@RestController
@RequestMapping("/api/v1/my-page2")
public class UserRestController {
	
	private final UserService userService;
    private final ItemService itemService;
    private final AppOrderService appOrderService;
    private final FavoriteService favoriteService;
    private final ReviewService reviewService;
    private final AuthUserResolver authUserResolver;
    private final MyPageOrderService myPageOrderService;

    public UserRestController(
    		
            UserService userService,
            ItemService itemService,
            AppOrderService appOrderService,
            FavoriteService favoriteService,
            ReviewService reviewService,
            AuthUserResolver authUserResolver,
            MyPageOrderService myPageOrderService
            
    ) {
    	
        this.userService = userService;
        this.itemService = itemService;
        this.appOrderService = appOrderService;
        this.favoriteService = favoriteService;
        this.reviewService = reviewService;
        this.authUserResolver = authUserResolver;
        this.myPageOrderService = myPageOrderService;
        
    }
    
    @GetMapping
    public ResponseEntity<UserReponse> myPage(Authentication authentication) {
        
    	User user = currentUser(authentication);
    	
        return ResponseEntity.ok(
                new UserReponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole(),
                        user.isEnabled(),
                        user.isBanned(),
                        user.getBanReason(),
                        user.getBannedAt()
                )
        );
    }
    
    @GetMapping("/selling")
    public ResponseEntity<?> mySellingItems(Authentication authentication) {
    	
        User user = currentUser(authentication);
        
        return ResponseEntity.ok(itemService.getItemsBySeller(user));
        
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<OrdersSummaryResponse>> myOrders (Authentication authentication) {
    	
    	User user = currentUser(authentication);
        
        return ResponseEntity.ok(
        		myPageOrderService.getOrderSummaries(user)
        	);
        
    }
    
    @GetMapping("/sales")
    public ResponseEntity<?> mySales(Authentication authentication) {
    	
        User user = currentUser(authentication);
        
        return ResponseEntity.ok(appOrderService.getOrdersBySeller(user));
        
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<?> myFavorites(Authentication authentication) {
    	
        User user = currentUser(authentication);
        
        return ResponseEntity.ok(favoriteService.getFavoriteItemsByUser(user));
        
    }
    
    @GetMapping("/reviews")
    public ResponseEntity<?> myReviews(Authentication authentication) {
        User user = currentUser(authentication);
        return ResponseEntity.ok(reviewService.getReviewsByReviewer(user));
    }

    private User currentUser(Authentication authentication) {
    	
        AuthUser authUser = authUserResolver.resolve(authentication);
        
        return userService.getUserByEmail(authUser.email())
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
        
    }


	
}
