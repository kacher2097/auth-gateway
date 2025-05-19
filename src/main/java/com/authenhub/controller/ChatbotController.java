package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.chatbot.ChatMessage;
import com.authenhub.bean.tool.chatbot.ChatbotRequest;
import com.authenhub.bean.tool.chatbot.ChatbotResponse;
import com.authenhub.service.interfaces.IChatbotService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tools/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final IChatbotService chatbotService;

    /**
     * Send a message to the chatbot and get a response
     *
     * @param request The chatbot request
     * @return The chatbot response
     */
    @PostMapping
    public ApiResponse<ChatbotResponse> sendMessage(@RequestBody ChatbotRequest request) {
        ChatbotResponse response = chatbotService.sendMessage(request);
        return ApiResponse.success(response);
    }

    /**
     * Get chat history for a session
     *
     * @param sessionId The session ID
     * @return The chat history
     */
    @GetMapping("/history/{sessionId}")
    public ApiResponse<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = chatbotService.getChatHistory(sessionId);
        return ApiResponse.success(history);
    }

    /**
     * Create a new chat session
     *
     * @param userId The user ID
     * @return The session ID
     */
    @PostMapping("/session")
    public ApiResponse<String> createChatSession(@RequestParam String userId) {
        String sessionId = chatbotService.createChatSession(userId);
        return ApiResponse.success(sessionId);
    }
}
