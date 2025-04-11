package com.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.member.model.MemberService;
import com.springbootmail.MailService;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Controller
public class VerifyCodeController {

    @Autowired
    private MailService mailService;

    @Autowired
    MemberService memberService;
    
    // 寄送註冊會員驗證碼
    @PostMapping("/sendCode")
    @ResponseBody
    public ResponseEntity<String> sendVerifyCode(@RequestParam("email") String email, HttpSession session) {
	    	if (email == null || email.trim().isEmpty()) {
	    	    return ResponseEntity.badRequest().body("請輸入Email");
	    	}
    		
	        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
	            return ResponseEntity.badRequest().body("Email 格式不正確");
	        }
	        
			if (memberService.findMember(email)) {
				return ResponseEntity.badRequest().body("此信箱已註冊");
			}
			
			
        // 產生 6 碼驗證碼
        String verifyCode = generateCode(6);

        // 寄信
        String subject = "寵愛牠註冊會員";
        String content = "您好，您的驗證碼是：" + verifyCode + "\n請在 5 分鐘內完成驗證。";
        mailService.sendPlainText(Collections.singleton(email), subject, content);

        // 存入 session，效期 5 分鐘
        session.setAttribute("verifyEmail", email);
        session.setAttribute("verifyCode", verifyCode);
		session.setAttribute("verifyCheck", false);
        session.setMaxInactiveInterval(300); // 單位：秒

        return ResponseEntity.ok("驗證碼已寄出");
    }

  
    
    // 寄送忘記密碼驗證碼
    @PostMapping("/sendPasswordCode")
    @ResponseBody
    public ResponseEntity<String> sendPasswordCode(@RequestParam("email") String email, HttpSession session) {
	     
	        
	        if (email == null || email.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body("請輸入Email");
	        }
	        
	        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
	            return ResponseEntity.badRequest().body("Email 格式不正確");
	        }
	        
	        if (!memberService.findMember(email)) {
				return ResponseEntity.badRequest().body("信箱未註冊 請註冊會員");
			}
	        
	        			
        // 產生 6 碼驗證碼
        String verifyCode = generateCode(6);

        // 寄信
        String subject = "寵愛牠會員重設密碼";
        String content = "您好，您的驗證碼是：" + verifyCode + "\n請在 5 分鐘內完成驗證。";
        mailService.sendPlainText(Collections.singleton(email), subject, content);

        // 存入 session，效期 5 分鐘
        session.setAttribute("verifyEmail", email);
        session.setAttribute("verifyCode", verifyCode);
		session.setAttribute("verifyCheck", false);
        session.setMaxInactiveInterval(300); // 單位：秒

        return ResponseEntity.ok("驗證碼已寄出");
    }



    
    
    @PostMapping("/checkCode")
    @ResponseBody
    public ResponseEntity<String> checkCode(
    		@RequestParam("code") String code,
    		@RequestParam("email") String email,    		
    		HttpSession session){
    	
        String sessionEmail = (String) session.getAttribute("verifyEmail");
        String sessionCode = (String) session.getAttribute("verifyCode");
        
    	if (sessionEmail == null || sessionCode == null) {
            return ResponseEntity.badRequest().body("驗證碼已過期或未寄出");
        }
    	
        if (!sessionEmail.equals(email)) {
            return ResponseEntity.badRequest().body("請填入信箱");
        }
        
        if (!sessionCode.equals(code)) {
            return ResponseEntity.badRequest().body("驗證碼錯誤");
        }
        
    	session.setAttribute("verifyCheck", true);
    	return ResponseEntity.ok("驗證成功");
    }
    
    private String generateCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }
        return sb.toString();
    }
}