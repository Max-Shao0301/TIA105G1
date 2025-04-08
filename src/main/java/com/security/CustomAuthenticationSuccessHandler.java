package com.security;

import com.member.model.MemberRepository;
import com.member.model.MemberVO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final MemberRepository memberRepository;

    public CustomAuthenticationSuccessHandler(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal(); // 取得當前用戶的身份信息（）
        // 確保 principal 是 DefaultOAuth2User 類型
        if (principal instanceof DefaultOAuth2User) {

            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) principal;
            // 從 oAuth2User 中取得 email
            String email = (String) oAuth2User.getAttributes().get("email");
            // 根據 email 查詢會員資料
            MemberVO memberVO = memberRepository.findByMemEmail(email);
            //如果手機和地址已填寫完成則導向首頁，若未完成則導向資料更新頁面
            if (memberVO != null && memberVO.getMemPhone() != null && memberVO.getAddress() != null) {
                response.sendRedirect("/");
            } else {
                response.sendRedirect("/updateMember");
            }
        }


    }
}
