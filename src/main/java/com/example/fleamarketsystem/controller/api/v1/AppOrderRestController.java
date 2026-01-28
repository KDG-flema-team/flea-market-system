package com.example.fleamarketsystem.controller.api.v1;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@RestController
@RequestMapping("/api/v1/orders")
public class AppOrderRestController {
	
	private final AppOrderService appOrderService;
    
    public AppOrderRestController(
            AppOrderService appOrderService
    ) {
        this.appOrderService = appOrderService;
    }
    
    /* =========================
    購入開始（PaymentIntent作成）
    ========================= */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePurchase(
    		
    		@LoginUser User buyer,
    		@RequestBody Map<String, Long> body
    		
    		) throws StripeException {
    	
    	Long itemId = body.get("itemId");
    	
    	if (itemId == null) {
         return ResponseEntity.badRequest()
                 .body(Map.of("error", "itemId is required"));
         }

        PaymentIntent paymentIntent =
                appOrderService.initiatePurchase(itemId, buyer);

        return ResponseEntity.ok(Map.of(
                "clientSecret", paymentIntent.getClientSecret(),
                "paymentIntentId", paymentIntent.getId()
        ));
    }
    
    /* =========================
    決済完了
    ========================= */
    @PostMapping("/complete")
    public ResponseEntity<?> completePurchase(
    		
    		@LoginUser User buyer,
    		@RequestBody Map<String, String> body
    		
    	) throws StripeException {
    	
    	String paymentIntentId = body.get("paymentIntentId");
    	
    	if (paymentIntentId == null) {
    		return ResponseEntity.badRequest()
    				.body(Map.of("error", "paymentIntentId is required"));
    		}
    	
    	appOrderService.completePurchase(paymentIntentId, buyer);

    	return ResponseEntity.ok(Map.of(
    			"message", "purchase completed"
    			));
    }
    
    /* =========================
    注文発送（出品者/管理者）
    ========================= */
    @PostMapping("/{id}/ship")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public ResponseEntity<?> shipOrder(	
    		
    		@LoginUser User operator,
    		@PathVariable Long id	
    		
    	) {
    	
    	appOrderService.markOrderAsShipped(id, operator);
    	return ResponseEntity.ok(Map.of("message", "order shipped"));
    }
    
    /* =========================
    Stripe Webhook
    ========================= */
    @PostMapping("/stripe-webhook")
    public ResponseEntity<Void> handleStripeWebhook(
    		
         @RequestBody String payload,
         @RequestHeader("Stripe-Signature") String signature
         
         ) {
    	
    	// TODO: 署名検証・イベント処理
    	System.out.println("Stripe Webhook: " + payload);
    	return ResponseEntity.ok().build();
    }


}
