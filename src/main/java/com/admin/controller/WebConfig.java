package com.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { // 實作 WebMvcConfigurer 介面，用來註冊攔截器

    private final LoginInterceptor loginInterceptor;


    public WebConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)//
                .addPathPatterns("/admin/home/page")  // 設定需要攔截的路徑
                .excludePathPatterns("/admin/login/page");  // 不攔截登入頁面
    }
}