package com.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "spring.ai")
public class AiProviderConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * AI模型提供商: openai, glm
     */
    private String provider = "openai";

    /**
     * GLM配置
     */
    private GlmConfig glm = new GlmConfig();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public GlmConfig getGlm() {
        return glm;
    }

    public void setGlm(GlmConfig glm) {
        this.glm = glm;
    }

    public static class GlmConfig {
        private String apiKey;
        private String apiUrl = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
        private ChatOptions chat = new ChatOptions();

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }

        public ChatOptions getChat() {
            return chat;
        }

        public void setChat(ChatOptions chat) {
            this.chat = chat;
        }
    }

    public static class ChatOptions {
        private String model = "glm-4.5";
        private Double temperature = 0.1;

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Double getTemperature() {
            return temperature;
        }

        public void setTemperature(Double temperature) {
            this.temperature = temperature;
        }
    }
}