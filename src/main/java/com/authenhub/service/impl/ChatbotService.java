package com.authenhub.service.impl;

import com.authenhub.bean.tool.chatbot.ChatbotRequest;
import com.authenhub.bean.tool.chatbot.ChatbotResponse;
import com.authenhub.bean.tool.chatbot.ChatMessage;
import com.authenhub.service.interfaces.IChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotService implements IChatbotService {

    // In-memory storage for chat sessions (would be replaced with a database in production)
    private final Map<String, List<ChatMessage>> chatSessions = new HashMap<>();

    @Override
    public ChatbotResponse sendMessage(ChatbotRequest request) {
        log.info("Sending message to chatbot with model: {}", request.getModel());
        
        // Create a unique ID for the message
        String messageId = UUID.randomUUID().toString();
        
        // Create a user message
        ChatMessage userMessage = ChatMessage.builder()
                .id(messageId)
                .content(request.getMessage())
                .sender("user")
                .timestamp(LocalDateTime.now())
                .build();
        
        // Add the message to the session
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }
        
        if (!chatSessions.containsKey(sessionId)) {
            chatSessions.put(sessionId, new ArrayList<>());
        }
        
        chatSessions.get(sessionId).add(userMessage);
        
        // Generate a response based on the message
        String responseContent = generateResponse(request.getMessage(), request.getModel());
        
        // Create a bot message
        ChatMessage botMessage = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .content(responseContent)
                .sender("bot")
                .timestamp(LocalDateTime.now())
                .build();
        
        // Add the bot message to the session
        chatSessions.get(sessionId).add(botMessage);
        
        // Build the response
        return ChatbotResponse.builder()
                .id(botMessage.getId())
                .message(responseContent)
                .model(request.getModel())
                .timestamp(LocalDateTime.now())
                .sessionId(sessionId)
                .tokensUsed(calculateTokens(request.getMessage(), responseContent))
                .build();
    }

    @Override
    public List<ChatMessage> getChatHistory(String sessionId) {
        log.info("Getting chat history for session: {}", sessionId);
        
        if (chatSessions.containsKey(sessionId)) {
            return chatSessions.get(sessionId);
        }
        
        return new ArrayList<>();
    }

    @Override
    public String createChatSession(String userId) {
        log.info("Creating chat session for user: {}", userId);
        
        String sessionId = UUID.randomUUID().toString();
        chatSessions.put(sessionId, new ArrayList<>());
        
        // Add a welcome message
        ChatMessage welcomeMessage = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .content("Xin chào! Tôi là trợ lý AI. Tôi có thể giúp gì cho bạn hôm nay?")
                .sender("bot")
                .timestamp(LocalDateTime.now())
                .build();
        
        chatSessions.get(sessionId).add(welcomeMessage);
        
        return sessionId;
    }
    
    private String generateResponse(String message, String model) {
        // Simple response generation based on keywords
        // In a real implementation, this would call an AI service like OpenAI, Claude, etc.
        
        message = message.toLowerCase();
        
        if (message.contains("xin chào") || message.contains("hello") || message.contains("hi")) {
            return "Xin chào! Tôi có thể giúp gì cho bạn?";
        } else if (message.contains("tên") && message.contains("gì")) {
            return "Tôi là trợ lý AI được phát triển bởi AuthenHub.";
        } else if (message.contains("thời tiết")) {
            return "Tôi không có khả năng kiểm tra thời tiết hiện tại. Bạn có thể hỏi tôi về các chủ đề khác không?";
        } else if (message.contains("giúp") || message.contains("help")) {
            return "Tôi có thể giúp bạn trả lời câu hỏi, viết nội dung, tạo mã, và nhiều thứ khác. Hãy cho tôi biết bạn cần gì!";
        } else if (message.contains("cảm ơn")) {
            return "Không có gì! Rất vui khi được giúp đỡ bạn.";
        } else if (message.contains("tạo mã") || message.contains("code")) {
            return "Để tạo mã, bạn cần cung cấp cho tôi yêu cầu cụ thể về loại mã bạn muốn tạo, ngôn ngữ lập trình, và chức năng của mã.";
        } else {
            return "Tôi hiểu yêu cầu của bạn. Đây là phản hồi của tôi dựa trên thông tin bạn cung cấp. Tôi có thể giúp bạn giải quyết vấn đề này bằng cách phân tích dữ liệu và đưa ra các gợi ý phù hợp. Nếu bạn cần thêm thông tin hoặc có yêu cầu cụ thể hơn, đừng ngần ngại cho tôi biết để tôi có thể hỗ trợ bạn tốt hơn.";
        }
    }
    
    private Integer calculateTokens(String input, String output) {
        // Simple token calculation (words * 1.3)
        // In a real implementation, this would use a proper tokenizer
        int inputWords = input.split("\\s+").length;
        int outputWords = output.split("\\s+").length;
        return (int) ((inputWords + outputWords) * 1.3);
    }
}
