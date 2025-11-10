package com.example.config;

import com.example.mcp.springai.OpenMeteoService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @desc:
 * @time: 2025-11-10 15:48:26
 * @author: Alina
 */
@Configuration
public class MCPSpringAiRegisterConfiguration {
    /**
     * 创建并配置一个天气工具回调提供者
     * 该方法通过接收一个OpenMeteoService实例来构建一个MethodToolCallbackProvider对象
     * 主要用于在调用OpenMeteoService的方法时提供回调功能
     *
     * @param openMete OpenMeteoService的实例，用于获取天气信息
     * @return 返回一个配置好的MethodToolCallbackProvider对象
     */
    @Bean
    public ToolCallbackProvider weatherTools(OpenMeteoService openMete){
        return MethodToolCallbackProvider.builder()
                .toolObjects(openMete)
                .build();
    }
}
