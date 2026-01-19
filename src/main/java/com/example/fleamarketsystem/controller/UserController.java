package com.example.fleamarketsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.FavoriteService;
import com.example.fleamarketsystem.service.ItemService;
import com.example.fleamarketsystem.service.ReviewService;
import com.example.fleamarketsystem.service.UserService;

@Controller
@RequestMapping("/my-page")
public class UserController {

	private final UserService userService;
	private final ItemService itemService;
	private final AppOrderService appOrderService;
	private final FavoriteService favoriteService;
	private final ReviewService reviewService;

	public UserController(UserService userService, ItemService itemService, AppOrderService appOrderService,
			FavoriteService favoriteService, ReviewService reviewService) {
		this.userService = userService;
		this.itemService = itemService;
		this.appOrderService = appOrderService;
		this.favoriteService = favoriteService;
		this.reviewService = reviewService;
	}

	private String getEmailFromAuthentication(Authentication authentication) {
		if (authentication == null) {
			throw new RuntimeException("User not authenticated");
		}

		Object principal = authentication.getPrincipal();
		
		// OAuth2Userの場合
		if (principal instanceof OAuth2User) {
			OAuth2User oauth2User = (OAuth2User) principal;
			String email = oauth2User.getAttribute("email");
			
			// GitHubなどemailがない場合のフォールバック
			if (email == null) {
				email = oauth2User.getAttribute("login") + "@github.com";
			}
			return email;
		}
		
		// UserDetailsの場合
		if (principal instanceof UserDetails) {
			return ((UserDetails) principal).getUsername();
		}
		
		throw new RuntimeException("Unsupported authentication type");
	}

	@GetMapping
	public String myPage(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("user", currentUser);
		return "my_page";
	}

	@GetMapping("/selling")
	public String mySellingItems(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("sellingItems", itemService.getItemsBySeller(currentUser));
		return "seller_items";
	}

	@GetMapping("/orders")
	public String myOrders(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("myOrders", appOrderService.getOrdersByBuyer(currentUser));
		return "buyer_app_orders";
	}

	@GetMapping("/sales")
	public String mySales(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("mySales", appOrderService.getOrdersBySeller(currentUser));
		return "seller_app_orders";
	}

	@GetMapping("/favorites")
	public String myFavorites(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("favoriteItems", favoriteService.getFavoriteItemsByUser(currentUser));
		return "my_favorites";
	}

	@GetMapping("/reviews")
	public String myReviews(Authentication authentication, Model model) {
		String email = getEmailFromAuthentication(authentication);
		User currentUser = userService.getUserByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));

		model.addAttribute("reviews", reviewService.getReviewsByReviewer(currentUser));
		return "user_reviews";
	}
}
