package com.authenhub.controller;

import com.authenhub.bean.ai.AiPromptRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.service.interfaces.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Groq AI", description = "API để tương tác với mô hình AI Groq")
public class GroqTestController {

    private final ChatModel chatModel;
    private final AiService aiService;

    public GroqTestController(@Qualifier("groqChatModel") ChatModel chatModel, AiService aiService) {
        this.chatModel = chatModel;
        this.aiService = aiService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    @Operation(summary = "Xử lý tin nhắn trò chuyện",
            description = "Xử lý tin nhắn trò chuyện và gửi phản hồi từ mô hình AI.")
    public ApiResponse<?> handleChatMessage(AiPromptRequest request) {
        return aiService.getAnswer(request);
    }

    @GetMapping("/question")
    @Operation(summary = "Gửi câu hỏi đơn giản",
            description = "Gửi một câu hỏi đơn giản đến mô hình AI và nhận phản hồi.")
    public String question(@RequestParam(name = "prompt") String prompt) {
        return chatModel.call(prompt);
    }

    // Cant run for groq
//    @GetMapping("/generate")
//    public void generateImg(HttpServletResponse httpServletResponse, @RequestParam(name = "prompt") String prompt) throws IOException {
//        ImageResponse imageResponse = openAiImageModel.call(
//                new ImagePrompt(
//                        prompt
//                )
//        );
//        String url = imageResponse.getResult().getOutput().getUrl();
//        httpServletResponse.sendRedirect(url);
//    }

    @PostMapping("/get-answer")
    @Operation(summary = "Lấy câu trả lời chi tiết",
            description = "Gửi một yêu cầu phức tạp đến mô hình AI và nhận phản hồi chi tiết.")
    public ApiResponse<?> question2(@RequestBody AiPromptRequest request) {
        return aiService.getAnswer(request);
    }
}
