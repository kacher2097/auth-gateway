package com.authenhub.bean.tool.chatbot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {
    private String id;
    private String message;
    private String model;
    private LocalDateTime timestamp;
    private String sessionId;
    private Integer tokensUsed;
}
