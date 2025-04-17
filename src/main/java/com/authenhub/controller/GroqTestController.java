package com.authenhub.controller;

import com.authenhub.bean.ai.AiPromptRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.service.interfaces.AiService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
public class GroqTestController {

    private final ChatModel chatModel;
    private final AiService aiService;

    public GroqTestController(@Qualifier("groqChatModel") ChatModel chatModel, AiService aiService) {
        this.chatModel = chatModel;
        this.aiService = aiService;
    }

    @GetMapping("/question")
    public String question(@RequestParam(name = "prompt") String prompt){
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
    public ApiResponse<?> question2(@RequestBody AiPromptRequest request){
        return aiService.getAnswer(request);
    }
}
