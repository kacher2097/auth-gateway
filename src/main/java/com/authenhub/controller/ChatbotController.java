package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.tool.chatbot.ChatMessage;
import com.authenhub.bean.tool.chatbot.ChatbotRequest;
import com.authenhub.bean.tool.chatbot.ChatbotResponse;
import com.authenhub.service.interfaces.IChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Chatbot", description = "API để tương tác với chatbot thông minh")
public class ChatbotController {

    private final IChatbotService chatbotService;

    /**
     * Send a message to the chatbot and get a response
     *
     * @param request The chatbot request
     * @return The chatbot response
     */
    @PostMapping
    @Operation(summary = "Gửi tin nhắn đến chatbot",
            description = "Gửi một tin nhắn đến chatbot và nhận phản hồi.")
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
    @Operation(summary = "Lấy lịch sử trò chuyện",
            description = "Lấy lịch sử trò chuyện của một phiên dựa trên ID phiên.")
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
    @Operation(summary = "Tạo phiên trò chuyện mới",
            description = "Tạo một phiên trò chuyện mới cho người dùng.")
    public ApiResponse<String> createChatSession(@RequestParam String userId) {
        String sessionId = chatbotService.createChatSession(userId);
        return ApiResponse.success(sessionId);
    }
}
