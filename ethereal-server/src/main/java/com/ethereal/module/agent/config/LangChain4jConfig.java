package com.ethereal.module.agent.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.api-key:}")
    private String apiKey;

    @Value("${langchain4j.open-ai.model-name:qwen-plus}")
    private String modelName;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your-api-key-here")) {
            log.warn("LangChain4j API Key 未配置，AI 功能将不可用。请在 application.yml 中设置 langchain4j.open-ai.api-key");
            // 返回一个不会实际调用的占位模型，避免注入失败
            return OpenAiChatModel.builder()
                    .baseUrl(baseUrl)
                    .apiKey("placeholder")
                    .modelName(modelName)
                    .temperature(0.7)
                    .maxTokens(1024)
                    .build();
        }
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .maxTokens(1024)
                .build();
    }
}
