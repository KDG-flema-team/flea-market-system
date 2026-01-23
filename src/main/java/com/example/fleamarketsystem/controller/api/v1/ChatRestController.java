package com.example.fleamarketsystem.controller.api.v1;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.dto.ChatRequest;
import com.example.fleamarketsystem.dto.ChatResponse;
import com.example.fleamarketsystem.entity.Chat;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.ChatService;
import com.example.fleamarketsystem.service.UserService;

@RestController
@RequestMapping("api/v1/chat")
public class ChatRestController {
	
	private final ChatService chatService;
    private final UserService userService;

    public ChatRestController(
    		
    	ChatService chatService,
    	UserService userService
    	
    	) {
    	
    	this.chatService = chatService;
        this.userService = userService;
        
        }
    
    @GetMapping("/{itemId}")
    public List<ChatResponse> getChats(
            @PathVariable Long itemId
    ) {
        return chatService.getChatMessagesByItem(itemId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @PostMapping("/{itemId}")
    public void sendMessage(
            @PathVariable Long itemId,
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User sender = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        chatService.sendMessage(itemId, sender, request.message());
    }
    
    private ChatResponse toResponse(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getItem().getId(),
                chat.getSender().getId(),
                chat.getSender().getName(),
                chat.getMessage(),
                chat.getCreatedAt()
        );
    }
	
}
