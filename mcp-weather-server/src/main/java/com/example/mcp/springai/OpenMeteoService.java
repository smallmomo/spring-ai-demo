package com.example.mcp.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @desc:
 * @time: 2025-11-10 15:50:31
 * @author: Alina
 */
@Service
@Slf4j
public class OpenMeteoService {

    private final RestTemplate restTemplate;

    private static final String WEATHER_TEMPLATE = "当前位置（纬度：%s，经度：%s）的天气信息：\n %s";

    public OpenMeteoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 根据给定的经纬度获取天气预报
     * 此方法使用RestTemplate调用外部天气API，获取JSON格式的天气信息，并将其格式化为字符串返回
     *
     * @param latitude  纬度，表示地理位置的南北位置
     * @param longitude 经度，表示地理位置的东西位置
     * @return 格式化后的天气预报信息字符串
     */
    @Tool(description = "根据给定的经纬度获取天气预报")
    public String getWeatherForecastByLocation(
            @ToolParam(description = "经纬度，例如：39.9042") String latitude,
            @ToolParam(description = "经纬度，例如：116.4074") String longitude
    ) {
        log.info("latitude: {}, longitude: {}", latitude, longitude);
        String url = "https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&hourly=temperature_2m&timezone=auto";
        String response = restTemplate.getForObject(url, String.class, latitude, longitude);
        log.info("response: {}", response);
        return String.format(WEATHER_TEMPLATE, latitude, longitude, response);
    }

    /**
     * 根据给定的经纬度获取空气质量信息
     *
     * @param latitude  纬度，表示地理位置的南北位置
     * @param longitude 经度，表示地理位置的东西位置
     * @return 空气质量信息字符串
     */
    @Tool(description = "根据经纬度获取空气质量信息")
    public String getAirQualityByLocation(
            @ToolParam(description = "经纬度，例如：39.9042") String latitude,
            @ToolParam(description = "经纬度，例如：116.4074") String longitude
    ) {
        // 模拟数据，实际应用中应调用真实API
        return "当前位置（纬度：" + latitude + "，经度：" + longitude + "）的空气质量：\n" +
                "- PM2.5: 15 μg/m³ (优)\n" +
                "- PM10: 28 μg/m³ (良)\n" +
                "- 空气质量指数(AQI): 42 (优)\n" +
                "- 主要污染物: 无";
    }
}
