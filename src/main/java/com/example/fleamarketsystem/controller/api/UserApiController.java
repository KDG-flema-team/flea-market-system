// package com.example.fleamarketsystem.controller.api;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.fleamarketsystem.annotation.LoginUser;
// import com.example.fleamarketsystem.entity.AppOrder;
// import com.example.fleamarketsystem.entity.User;
// import com.example.fleamarketsystem.service.AppOrderService;
// import com.example.fleamarketsystem.service.UserService;

// import lombok.RequiredArgsConstructor;


// // 注: UserRestControllerと統合したため無効化
// // @RestController
// @RequestMapping("/api/v1")
// @RequiredArgsConstructor
// @CrossOrigin(origins = "*", maxAge = 3600)
// public class UserApiController {

//     private final UserService userService;
//     private final PasswordEncoder passwordEncoder;
//     private final AppOrderService appOrderService;

//     /**
//      * 現在のユーザー情報を取得します
//      */
//     @GetMapping("/me")
//     public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
//         try {
//             if (jwt == null) {
//                 return createErrorResponse("User not authenticated", 401);
//             }
//             User user = userService.getOrCreateUserFromAuth0(jwt);
            
//             return ResponseEntity.ok(createUserResponse(jwt, user));
//         } catch (Exception e) {
//             return createErrorResponse("Failed to get current user", e.getMessage());
//         }
//     }

//     /**
//      * ユーザープロフィールを取得します
//      */
//     @GetMapping("/my-page")
//     public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal Jwt jwt, @LoginUser User loginUser) {
//         try {
//             if (jwt == null) {
//                 return createErrorResponse("User not authenticated", 401);
//             }
//             if (loginUser == null) {
//                 return createErrorResponse("User not found", 404);
//             }
            
//             Map<String, Object> response = new HashMap<>();
//             response.put("userId", loginUser.getId());
//             response.put("email", loginUser.getEmail());
//             response.put("name", loginUser.getName());
//             response.put("role", loginUser.getRole());
//             response.put("rank", loginUser.getRank());
//             response.put("enabled", loginUser.isEnabled());
//             response.put("banned", loginUser.isBanned());
//             response.put("description", loginUser.getDescription());
//             // JWT情報も含める場合
//             response.put("auth0Id", jwt.getSubject());
//             response.put("issuer", jwt.getIssuer());
//             return ResponseEntity.ok(response);
//             // return ResponseEntity.ok(createProfileResponse(jwt, user));
//         } catch (Exception e) {
//             return createErrorResponse("Failed to get user profile", e.getMessage());
//         }

//     }
    
//     @PutMapping("/my-page")
//     public ResponseEntity<?> updateMyProfile(
    		
//     		@AuthenticationPrincipal Jwt jwt,
//     		@LoginUser User loginUser,
//     		@RequestBody Map<String, Object> body
    		
//     		) {
    	
//     	try {
//             if (jwt == null) {
//                 return createErrorResponse("ユーザーが認証されていません", 401);
//             }

//             if (loginUser == null) {
//                 return createErrorResponse("ユーザーが見つかりません", 404);
//             }

//             String name = (String) body.get("name");
//             String description = (String) body.get("description");

//             if (name != null && !name.isBlank()) {
//                 loginUser.setName(name);
//             }

//             if (description != null) {
//                 loginUser.setDescription(description);
//             }

//             userService.saveUser(loginUser); // 🔽 保存処理

//             Map<String, Object> response = new HashMap<>();
//             response.put("userId", loginUser.getId());
//             response.put("name", loginUser.getName());
//             response.put("description", loginUser.getDescription());

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             return createErrorResponse("プロフィールの更新に失敗しました", e.getMessage());
//         }


//     }

//     /**
//      * ユーザーの購入履歴を取得します
//      */
//     @GetMapping("/my-page/orders")
//     public ResponseEntity<?> getMyOrders(@AuthenticationPrincipal Jwt jwt, @LoginUser User loginUser) {
//         try {
//             if (jwt == null) {
//                 return createErrorResponse("ユーザーが認証されていません", 401);
//             }
//             if (loginUser == null) {
//                 return createErrorResponse("ユーザーが見つかりません", 404);
//             }

//             List<AppOrder> orders = appOrderService.getOrdersByBuyer(loginUser);

//             Map<String, Object> response = new HashMap<>();
//             response.put("orders", orders);

//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             return createErrorResponse("購入履歴の取得に失敗しました", e.getMessage());
//         }
//     }

//     /**
//      * ユーザー情報のレスポンスマップを作成します
//      */
//     private ResponseEntity<Map<String, Object>> createErrorResponse(String message, int status) {
//         Map<String, Object> body = new HashMap<>();
//         body.put("error", message);
//         body.put("status", status);
//         return ResponseEntity.status(status).body(body);
//     }

//     private ResponseEntity<Map<String, Object>> createErrorResponse(String message, String detail) {
//         Map<String, Object> body = new HashMap<>();
//         body.put("error", message);
//         body.put("detail", detail);
//         return ResponseEntity.status(500).body(body);
//     }
//     private Map<String, Object> createUserResponse(Jwt jwt, User user) {
//         Map<String, Object> response = new HashMap<>();
//         response.put("sub", jwt.getSubject());
//         response.put("email", jwt.getClaimAsString("email"));
//         response.put("name", jwt.getClaimAsString("name"));
//         response.put("userId", user.getId());
//         response.put("claims", jwt.getClaims());
//         return response;
//     }

//     /**
//      * プロフィール情報のレスポンスマップを作成します
//      */
//     private Map<String, Object> createProfileResponse(Jwt jwt, User user) {
//         Map<String, Object> response = new HashMap<>();
//         response.put("userId", user.getId());
//         response.put("email", user.getEmail());
//         response.put("name", user.getName());
//         response.put("role", user.getRole());
//         response.put("rank", user.getRank());
//         response.put("enabled", user.isEnabled());
//         response.put("banned", user.isBanned());
//         response.put("description", user.getDescription());
//         // JWT情報も含める場合
//         response.put("auth0Id", jwt.getSubject());
//         response.put("issuer", jwt.getIssuer());
//         return response;
//     }

// }