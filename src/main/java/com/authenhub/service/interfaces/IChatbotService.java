package com.authenhub.service.interfaces;

import com.authenhub.bean.tool.chatbot.ChatbotRequest;
import com.authenhub.bean.tool.chatbot.ChatbotResponse;
import com.authenhub.bean.tool.chatbot.ChatMessage;

import java.util.List;

public interface IChatbotService {
    /**
     * Send a message to the chatbot and get a response
     * @param request The chatbot request
     * @return The chatbot response
     */
    ChatbotResponse sendMessage(ChatbotRequest request);
    
    /**
     * Get chat history for a session
     * @param sessionId The session ID
     * @return The chat history
     */
    List<ChatMessage> getChatHistory(String sessionId);
    
    /**
     * Create a new chat session
     * @param userId The user ID
     * @return The session ID
     */
    String createChatSession(String userId);
}
