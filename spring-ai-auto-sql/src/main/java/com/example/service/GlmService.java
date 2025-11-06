package com.example.service;

import com.example.config.AiProviderConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GlmService {

    @Autowired
    private AiProviderConfig aiProviderConfig;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateSql(String prompt) {
        try {
            String apiUrl = aiProviderConfig.getGlm().getApiUrl();
            String apiKey = aiProviderConfig.getGlm().getApiKey();

            // 构建请求体
            Map<String, Object> requestBody = Map.of(
                    "model", aiProviderConfig.getGlm().getChat().getModel(),
                    "messages", Collections.singletonList(Map.of("role", "user", "content", prompt)),
                    "temperature", aiProviderConfig.getGlm().getChat().getTemperature());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class);

            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                Object choicesObj = responseBody.get("choices");

                // 处理choices字段可能是ArrayList的情况
                if (choicesObj instanceof ArrayList) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) choicesObj;
                    if (!choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        String content = (String) message.get("content");

                        // 清理SQL内容，移除```sql和```标记
                        content = content.trim();
                        if (content.startsWith("```sql")) {
                            content = content.substring(6).trim();
                        }
                        if (content.endsWith("```")) {
                            content = content.substring(0, content.length() - 3).trim();
                        }

                        return content;
                    }
                } else if (choicesObj instanceof Map) {
                    // 处理choices字段可能是Map的情况
                    Map<String, Object> choiceMap = (Map<String, Object>) choicesObj;
                    Map<String, Object> choice = (Map<String, Object>) choiceMap.get("0");
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String content = (String) message.get("content");

                    // 清理SQL内容，移除```sql和```标记
                    content = content.trim();
                    if (content.startsWith("```sql")) {
                        content = content.substring(6).trim();
                    }
                    if (content.endsWith("```")) {
                        content = content.substring(0, content.length() - 3).trim();
                    }

                    return content;
                }

                throw new RuntimeException("GLM API响应格式异常: 无法解析choices字段");
            } else {
                throw new RuntimeException("GLM API调用失败: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GLM API调用失败: " + e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("GLM API响应解析失败: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("GLM模型调用失败: " + e.getMessage(), e);
        }
    }
}