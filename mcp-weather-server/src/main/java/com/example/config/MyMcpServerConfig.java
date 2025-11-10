package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import org.springframework.ai.mcp.server.autoconfigure.McpServerProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * @desc:
 * @time: 2025-11-10 15:49:19
 * @author: Alina
 */
@Configuration
public class MyMcpServerConfig {

    @Bean
    @Primary
    public WebMvcSseServerTransportProvider webMvcSseServerTransportProvider(
            ObjectProvider<ObjectMapper> objectMapperProvider, McpServerProperties serverProperties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new WebMvcSseServerTransportProvider(objectMapper, serverProperties.getSseMessageEndpoint(),
                serverProperties.getSseEndpoint());
    }

    @Bean
    public RouterFunction<ServerResponse> mvcMcpRouterFunction(WebMvcSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }
}
