package com.authenhub.service;

import com.authenhub.bean.ChatMessage;
import com.authenhub.bean.ai.AiPromptRequest;
import com.authenhub.bean.ai.AiResponseJson;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.config.application.JsonMapper;
import com.authenhub.service.interfaces.AiService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GroqAiServiceImpl implements AiService {

    private final ChatModel chatModel;
    private final JsonMapper jsonMapper;
    private final List<ChatMessage> conversationHistory = new ArrayList<>();

    public GroqAiServiceImpl(@Qualifier("groqChatModel") ChatModel chatModel, JsonMapper jsonMapper) {
        this.chatModel = chatModel;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public ApiResponse<?> getAnswer(AiPromptRequest request) {

        conversationHistory.add(ChatMessage.builder()
                .role("assistant")
                .content(request.getPrompt())
                .build());

        StringBuilder context = new StringBuilder();
        for (ChatMessage msg : conversationHistory) {
            context.append(msg.getRole())
                    .append(": ")
                    .append(msg.getContent())
                    .append("\n");
        }

        try {
            String response = chatModel.call(context.toString());
            log.info("Get Response from AI success {}", response);
//            ChatClient client = ChatClient.builder(chatModel)
//                    .build().prompt()
//                    .advisors(new QuestionAnswerAdvisor(vectorStore))
//                    .user(userText)
//                    .call()
//                    .chatResponse();

            conversationHistory.add(ChatMessage.builder()
                    .role("assistant")
                    .content(request.getPrompt())
                    .build());

            // Giới hạn kích thước lịch sử (ví dụ: 10 tin nhắn gần nhất)
            if (conversationHistory.size() > 20) {
                log.info("Clear conversation history for > 20 messages");
                conversationHistory.subList(0, conversationHistory.size() - 20).clear();
            }

            // Gọi hàm xử lý chuỗi
            String json = extractJson(response);

            // Kiểm tra kết quả
            if (json != null) {
                System.out.println("✅ JSON Extracted:\n" + json);

                // Parse JSON để kiểm tra
                String finalS = parseJson(json);
                return ApiResponse.success(finalS);
            } else {
                System.out.println("❌ Không tìm thấy JSON hợp lệ!");
            }

            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            return null;
        }
    }

    public String extractJson(String text) {
        String jsonRegex = "```json\\s*(\\{.*})\\s*```";
        Pattern pattern = Pattern.compile(jsonRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1); // Lấy phần JSON bên trong
        }

        // Nếu không có ```json, tìm JSON theo cặp {}
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1).trim();
        }
        return null;
    }

    public String parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String title = jsonObject.getString("title");
//            String excerpt = jsonObject.getString("excerpt");
            String content = jsonObject.getString("content");

            // In kết quả
            System.out.println("Title: " + title);
//            System.out.println("Excerpt: " + excerpt);
            System.out.println("Content: " + content);
            AiResponseJson aiResponseJson = new AiResponseJson();
            aiResponseJson.setTitle(title);
            aiResponseJson.setContent(content);
            return jsonMapper.toJson(aiResponseJson);
        } catch (Exception e) {
            System.out.println("❌ JSON không hợp lệ: " + e.getMessage());
            return null;
        }
    }

    public List<ChatMessage> getConversationHistory() {
        return new ArrayList<>(conversationHistory);
    }

    public void clearHistory() {
        conversationHistory.clear();
        log.info("Conversation history cleared");
    }
}
