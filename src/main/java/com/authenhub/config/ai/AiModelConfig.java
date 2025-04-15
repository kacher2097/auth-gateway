package com.authenhub.config.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Getter
@RequiredArgsConstructor
public class AiModelConfig {

    @Value("${spring.ai.groq.api-key}")
    private String groqApiKey;

    @Value("${spring.ai.groq.base-url}")
    private String groqBaseUrl;

    @Value("${spring.ai.groq.chat.model}")
    private String groqChatModel;

    @Value("${spring.ai.groq.chat.temperature}")
    private Double groqChatTemperature;

    @Value("${spring.ai.groq.chat.max-tokens}")
    private Integer groqMaxToken;

    @Value("${spring.ai.groq.chat.top-p}")
    private Double groqTopP;

    @Value("${spring.ai.groq.chat.presence-penalty}")
    private Double groqPresencePenalty;

    @Value("${spring.ai.groq.chat.frequency-penalty}")
    private Double groqFrequencyPenalty;

    @Bean("groqChatModel")
    public ChatModel groqChatModel() {
        log.info("Begin init Groq AI with chat model {}", groqChatModel);
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(groqChatModel)
                .temperature(groqChatTemperature)
                .maxTokens(groqMaxToken)
                .topP(groqTopP)
                .presencePenalty(groqPresencePenalty)
                .frequencyPenalty(groqFrequencyPenalty)
                .build();
        OpenAiApi openAiApi = OpenAiApi.builder()
                .apiKey(groqApiKey)
                .baseUrl(groqBaseUrl)
                .build();
        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();
        log.info("Config done for Groq");
        return openAiChatModel;
    }

    @Bean("openAiImageModel")
    public OpenAiImageModel groqImgModel() {
        log.info("Begin init Groq AI with chat model {}", groqChatModel);
//        OpenAiImageOptions options = OpenAiImageOptions.builder()
//                .model(groqChatModel)
//                .height(1080)
//                .width(2040)
//                .build();
        OpenAiImageApi openAiImageApi = OpenAiImageApi.builder()
                .apiKey(groqApiKey)
                .baseUrl(groqBaseUrl)
                .build();
        OpenAiImageModel openAiChatModel = new OpenAiImageModel(openAiImageApi);

        log.info("Config done for Groq img model");
        return openAiChatModel;
    }
}
