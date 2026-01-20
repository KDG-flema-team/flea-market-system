package com.example.fleamarketsystem.controller.api.v1;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.service.AppOrderService;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {
	
	private final AppOrderService appOrderService;
	
	public AdminRestController(AppOrderService appOrderService) {
		this.appOrderService = appOrderService;
	}
	
	

}
