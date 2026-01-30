// src/main/java/com/example/fleamarketsystem/controller/api/AdminUserController.java
package com.example.fleamarketsystem.controller.api.v1;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.AdminUserResponse;
import com.example.fleamarketsystem.dto.BanRequest;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.AdminUserService;

@RestController
@RequestMapping("api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserRestController {

    private final AdminUserService service;

    public AdminUserRestController(AdminUserService service) {
        this.service = service;
    }

    /**
     * List users
     * GET /admin/users
     */
    @GetMapping
    public List<AdminUserResponse> list(
    		@LoginUser User adminUser,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", defaultValue = "id") String sort
    ) {
        List<User> list = service.listAllUsers();

        if (StringUtils.hasText(q)) {
            String qq = q.toLowerCase();
            list = list.stream()
                    .filter(u ->
                            (u.getName() != null && u.getName().toLowerCase().contains(qq)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(qq))
                    )
                    .toList();
        }

        list = switch (sort) {
            case "name" -> list.stream()
                    .sorted(Comparator.comparing(
                            User::getName,
                            Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
            case "email" -> list.stream()
                    .sorted(Comparator.comparing(
                            User::getEmail,
                            Comparator.nullsLast(String::compareToIgnoreCase)))
                    .toList();
            case "banned" -> list.stream()
                    .sorted(Comparator.comparing(User::isBanned).reversed())
                    .toList();
            default -> list;
        };
        
        return list.stream()
        		.map(AdminUserResponse::fromEntity)
                .toList();
    }

    /**
     * Get user details
     * GET /admin/users/{id}
     */
    @GetMapping("/{id}")
    public AdminUserResponse detail(
    		@LoginUser User adminUser,
    		@PathVariable Long id
    ) {
        User user = service.findUser(id);
        
        return AdminUserResponse.fromEntity(user);
    }

    /**
     * Ban / Unban user
     * POST /admin/users/{id}/ban
     */
    @PostMapping("/{id}/ban")
    public void updateBanStatus(
    		@LoginUser User adminUser,
            @PathVariable Long id,
            @RequestBody BanRequest request
    ) {
        Long adminId = adminUser.getId();

        if (request.banned()) {
            service.banUser(
                    id,
                    adminId,
                    request.reason(),
                    true
            );
        } else {
            service.unbanUser(id);
        }
    }
}
