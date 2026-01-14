package com.example.fleamarketsystem.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.AppOrderRequest;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AppOrderService;
import com.example.fleamarketsystem.service.ItemService;
import com.example.fleamarketsystem.service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

@RestController
@RequestMapping("/api/orders")
public class AppOrderRestController {
	
	private final AppOrderService appOrderService;
    private final UserService userService;
    private final ItemService itemService;
    
    @Value("${stripe.public.key}")
    private String stripePublicKey;
    
    public AppOrderRestController(AppOrderService appOrderService, UserService userService, ItemService itemService) {
        this.appOrderService = appOrderService;
        this.userService = userService;
        this.itemService = itemService;
    }
    
    /**
     * 購入を開始し、Stripe PaymentIntentを作成
     * POST /api/orders/initiate-purchase
     */
    @PostMapping("/initiate-purchase")
    public ResponseEntity<?> initiatePurchase(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AppOrderRequest request) {
    	System.out.println("itemId = " + request);
        try {
            User buyer = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Buyer not found"));
            
            PaymentIntent paymentIntent = appOrderService.initiatePurchase(request.itemId(), buyer);
            
            return ResponseEntity.ok(Map.of(
                "clientSecret", paymentIntent.getClientSecret(),
                "itemId", request.itemId(),
                "stripePublicKey", stripePublicKey,
                "paymentIntentId", paymentIntent.getId()
            ));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "決済処理の初期化に失敗しました: " + e.getMessage()));
        }
    }
    
    /**
     * 決済完了処理
     * POST /api/orders/complete-purchase
     */
    @PostMapping("/complete-purchase")
    public ResponseEntity<?> completePurchase(
            @RequestParam("paymentIntentId") String paymentIntentId) {
        try {
            appOrderService.completePurchase(paymentIntentId);
            
            return appOrderService.getLatestCompletedOrderId()
                    .map(orderId -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "商品を購入しました！",
                        "orderId", orderId
                    )))
                    .orElseGet(() -> ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "購入は完了しましたが、注文IDの取得に失敗しました。"
                    )));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "決済処理中にエラーが発生しました: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Stripe Webhookエンドポイント
     * POST /api/orders/stripe-webhook
     */
    @PostMapping("/stripe-webhook")
    public ResponseEntity<Void> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        // 実際のアプリケーションでは、Webhookの署名を検証し、
        // payment_intent.succeeded、payment_intent.payment_failedなどのイベントを処理します
        System.out.println("Received Stripe Webhook: " + payload);
        
        // TODO: 署名検証とイベント処理の実装
        // Example:
        // Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        // if ("payment_intent.succeeded".equals(event.getType())) { ... }
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * 注文を発送済みにマーク
     * POST /api/orders/{id}/ship
     */
    @PostMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(@PathVariable("id") Long orderId) {
        try {
            appOrderService.markOrderAsShipped(orderId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "商品を発送済みにしました。"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Stripe公開鍵を取得
     * GET /api/orders/stripe-config
     */
    @GetMapping("/stripe-config")
    public ResponseEntity<?> getStripeConfig() {
        return ResponseEntity.ok(Map.of("stripePublicKey", stripePublicKey));
    }

}