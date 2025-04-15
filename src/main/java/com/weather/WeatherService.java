package com.weather;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final RestTemplate restTemplate; // 用於發送 HTTP 請求和接收回應

    @Autowired
    private final OpenWeatherProperties properties;

    public WeatherService(RestTemplateBuilder builder, OpenWeatherProperties properties) {
        this.restTemplate = builder.build(); // 使用 RestTemplateBuilder 建立 RestTemplate
        this.properties = properties; // 讀取properties中的 OpenWeather API 相關屬性
    }

    public WeatherResponse getWeather(String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric&lang=zh_tw",
                properties.getUrl(), city, properties.getKey()); // 組合 API 請求的 URL

        return restTemplate.getForObject(url, WeatherResponse.class); // 發送 GET 請求並將回應轉換為 WeatherResponse 物件
    }

}
