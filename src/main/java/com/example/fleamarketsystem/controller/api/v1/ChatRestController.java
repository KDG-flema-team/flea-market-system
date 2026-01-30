package com.example.fleamarketsystem.controller.api.v1;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fleamarketsystem.annotation.LoginUser;
import com.example.fleamarketsystem.dto.ChatRequest;
import com.example.fleamarketsystem.dto.ChatResponse;
import com.example.fleamarketsystem.entity.Chat;
import com.example.fleamarketsystem.entity.User;
import com.example.fleamarketsystem.service.ChatService;

@RestController
@RequestMapping("api/v1/chat")
public class ChatRestController {
	
	private final ChatService chatService;

    public ChatRestController(
    		
    	ChatService chatService
    	
    	) {
    	
    	this.chatService = chatService;
        
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
            @LoginUser User sender
            
    ) {
    	
    	System.out.println(sender);

        chatService.sendMessage(
        		itemId,
        		sender,
        		request.message());
        
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
