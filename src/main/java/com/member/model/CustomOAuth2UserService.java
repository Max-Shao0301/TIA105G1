package com.member.model;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service("customOAuth2UserService")
public class CustomOAuth2UserService  extends DefaultOAuth2UserService {

    @Autowired
    private MemberService memberService;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //呼叫父類別的loadUser方法，透過accessToken取得使用者資料
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //從OAuth2User中取得使用者屬性，存到attributes中
        var attributes = oAuth2User.getAttributes();
        //從attributes中取得email和name
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");


        //登入流程結束後，取得的基本資訊只會存在於OAuth2User中，配合保存到會員資料表中需要儲存一些session資訊，傳給service，由service操作session存放或讀取登入使用者資料
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()) // 取得當前請求的屬性
                .getRequest() // 取得HttpServletRequest
                .getSession(); // 取得HttpSession


        memberService.saveOAuth2Member(email, name, session);
        return oAuth2User;
    }
}
