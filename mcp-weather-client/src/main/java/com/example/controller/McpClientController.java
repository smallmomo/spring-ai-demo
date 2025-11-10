package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * @desc:
 * @time: 2025-11-10 16:44:49
 * @author: Alina
 */
@RestController
@RequestMapping("/mcp")
public class McpClientController {

    private static final Logger log = LoggerFactory.getLogger(McpClientController.class);
    private final ChatClient chatClient;

    @Autowired
    public McpClientController(ChatClient.Builder mcpChatClientBuilder, ToolCallbackProvider tools) {
        this.chatClient = mcpChatClientBuilder.defaultTools(tools).build();
    }

    @PostMapping("/ask")
    public String ask(@RequestBody Map<String, String> question) {
        String response = chatClient.prompt(question.get("question")).call().content();
        log.info("response: {}", response);
        return "question: " + question + " <hr>"
                + response
                .replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>")
                .replaceAll("\n", "<br/>");
    }

    @GetMapping("/askModel")
    public String ask2(@RequestParam String question) {
        return chatClient.prompt().user(question).call().content();
    }
}
