package com.example.service;

import com.example.config.AiProviderConfig;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlGenerationService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private AiProviderConfig aiProviderConfig;

    @Autowired
    private GlmService glmService;

    public String generateSql(String prompt) {
        String provider = aiProviderConfig.getProvider();

        if ("glm".equals(provider)) {
            return glmService.generateSql(prompt);
        } else {
            return generateWithOpenai(prompt);
        }
    }

    private String generateWithOpenai(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}