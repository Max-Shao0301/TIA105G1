package com.mfa.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.mfa.service.TotpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private TotpService totpService;

    @Autowired
    private MemberService memberService;

    //設定二階段驗證頁面
    @GetMapping("/QRGenerate")
    public String generateSecret(HttpSession session, Model model, RedirectAttributes redirectAttributes) throws Exception {
        Integer memId = (Integer) session.getAttribute("memId"); //從session中取得會員ID
        MemberVO memberVO = memberService.getOneMember(memId);
        //如果會員已經有密鑰，則不需要再產生新的密鑰，轉向回首頁
        if (memberVO.getSecret() != null && !memberVO.getSecret().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "二階段登入已設定完成，請勿重複設定");
            return "redirect:/member";
        }

        String user = memberVO.getMemEmail();
        String secret = totpService.generateSecret();// 產生密鑰
        String qrCode = totpService.getQRCodeImage(secret, user, "PawCares"); // 產生QR Code，使用者信箱及APP名稱加入到驗證器

        // 將密鑰存入資料庫
        memberVO.setSecret(secret);
        memberService.updateMember(memberVO);
        // 將QR Code傳遞到前端
        model.addAttribute("qrCode", qrCode);

        return "front-end/mfa";
    }
    //二階段登入頁面
    @GetMapping("/mfaLoginPage")
    public String mfaLogin(){
        return "front-end/mfaLoginPage";
    }

    //二階段登入驗證
    @PostMapping("/mfa/login")
    public String Otp(String otp, HttpSession session, Model model) {
        Integer memId = (Integer) session.getAttribute("memId");
        MemberVO memberVO = memberService.getOneMember(memId);
        String secret = memberVO.getSecret();
        // 驗證OTP碼
        if (totpService.verifyCode(secret, otp)) {
            // 驗證成功，將會員資訊存入session
            session.setAttribute("memId", memberVO.getMemId());
            session.setAttribute("memName", memberVO.getMemName());
            return "redirect:/";
        } else {
            // 驗證失敗，顯示錯誤訊息
            model.addAttribute("error", "驗證碼錯誤，請重新輸入");
            return "front-end/mfaLoginPage";
        }
    }

    //放棄設定二階段登入
    @GetMapping("/mfa/cancel")
    public String cancel(HttpSession session) {
        Integer memId = (Integer) session.getAttribute("memId");
        MemberVO memberVO = memberService.getOneMember(memId);
        //若使用者放棄設定則把密鑰設為null並且返回
        if (memberVO.getSecret() != null && !memberVO.getSecret().isEmpty()) {
            memberVO.setSecret(null);
            memberService.updateMember(memberVO);
        }
        return "redirect:/";
    }
}