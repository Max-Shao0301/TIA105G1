package com.member.controller;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (request.getSession().getAttribute("memId") == null ) {
			HttpSession session = request.getSession();
			session.setAttribute("loginError", "請先登入才能瀏覽此頁！");
			response.sendRedirect("/login");
			    return false;
		}

		return true; // 放行
	}

}
