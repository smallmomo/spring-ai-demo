package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @desc: 天气服务端应用
 * @time: 2025-11-10 15:33:37
 * @author: Alina
 */
@SpringBootApplication
@EnableCaching
public class McpWeatherServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpWeatherServerApplication.class, args);
    }

}
