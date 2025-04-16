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
            if (memberVO != null) {
                // 若有設定二階段驗證 secret，導向驗證器登入頁面
                if (memberVO.getSecret() != null && !memberVO.getSecret().isEmpty()) {
                    response.sendRedirect("/mfaLoginPage");
                } else if (memberVO.getMemPhone() != null && memberVO.getAddress() != null) {
                    // 若手機和地址有填寫，導向首頁
                    response.sendRedirect("/");
                } else {
                    // 否則導向更新會員資料頁面
                    response.sendRedirect("/member/updateMember");
                }
            } else {
                 // 若會員資料不存在，導向首頁 LINE使用
                   response.sendRedirect("/");
            }
        }
    }
}
