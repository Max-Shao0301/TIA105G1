package com.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor { // 實作 HandlerInterceptor 介面，用來攔截請求

    @Override // 在請求處理之前攔截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 檢查 session 中是否有 "admin"
        if (request.getSession().getAttribute("admin") == null) {
            // 如果沒有登入，重定向到登入頁面
            response.sendRedirect("/admin/login/page");
            return false;  // 阻止請求繼續
        }
        return true;  // 允許請求繼續
    }
}
