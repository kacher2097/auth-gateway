package com.authenhub.bean.tool.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    private String userId;
    private String message;
    private String model; // gpt-3.5, gpt-4, claude, llama
    private List<ChatMessage> history;
    private String sessionId;
}
