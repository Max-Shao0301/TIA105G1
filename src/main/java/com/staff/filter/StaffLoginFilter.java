package com.staff.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class StaffLoginFilter implements Filter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final String[] excludedPaths = {"/staff/login", "/staff/forgotPassword", "/staff/login/forgotSetPassword", "/static/**"}; 

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String path = httpRequest.getRequestURI();

        boolean isExcluded = false;
        for (String excludedPath : excludedPaths) {
            if (pathMatcher.match(excludedPath, path)) {
                isExcluded = true;
                break;
            }
        }


        if (path.startsWith("/staff/") && !isExcluded) {
            if (session == null || session.getAttribute("staffId") == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/staff/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }


}