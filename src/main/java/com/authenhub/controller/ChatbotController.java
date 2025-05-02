package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.chatbot.ChatbotRequest;
import com.authenhub.bean.tool.chatbot.ChatbotResponse;
import com.authenhub.bean.tool.chatbot.ChatMessage;
import com.authenhub.service.interfaces.IChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tools/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final IChatbotService chatbotService;

    /**
     * Send a message to the chatbot and get a response
     * @param request The chatbot request
     * @return The chatbot response
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ChatbotResponse>> sendMessage(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatbotService.sendMessage(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get chat history for a session
     * @param sessionId The session ID
     * @return The chat history
     */
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = chatbotService.getChatHistory(sessionId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * Create a new chat session
     * @param userId The user ID
     * @return The session ID
     */
    @PostMapping("/session")
    public ResponseEntity<ApiResponse<String>> createChatSession(@RequestParam String userId) {
        String sessionId = chatbotService.createChatSession(userId);
        return ResponseEntity.ok(ApiResponse.success(sessionId));
    }
}
