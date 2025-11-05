package com.example.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlGenerationService {

    @Autowired
    private ChatClient chatClient;

    public String generateSql(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}