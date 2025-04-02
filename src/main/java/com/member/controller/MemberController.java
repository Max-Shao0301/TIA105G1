package com.member.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;
import com.springbootmail.MailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/updateMemberStatus")
    public String updateMemberStatus(@RequestParam("member_id") Integer memId){
        memberService.deleteMember(memId);

        return "redirect:/admin/home/page";
    }
    
	@GetMapping("/forgetpassword")
	public String forgetpassword() {
		return "/front-end/forgetpassword";
	}
	
	@GetMapping("/member")
	public String member(HttpSession session, Model model) {
		Integer memId = (Integer) session.getAttribute("memId");
//		System.out.println(memId);
		MemberVO memberVO = memberService.getOneMember(memId);
		model.addAttribute("memberVO",memberVO);
		return "/front-end/member";
	}
    
	@GetMapping("/register")
	public String register(Model model, HttpSession session) {
	    session.removeAttribute("verifyEmail");
	    session.removeAttribute("verifyCode");
	    session.removeAttribute("verifyCheck");

	    model.addAttribute("memberDTO", new MemberDTO());
	    return "/front-end/register";
	}
	
	//註冊
	@PostMapping("/register")
	public String register(@ModelAttribute @Valid MemberDTO memberDTO,
	                       BindingResult bindingResult,
	                       HttpSession session,
	                       Model model) {
		Integer count = 0;
		
	    // 確認是否完成 email 驗證
	    Boolean verifyCheck = (Boolean) session.getAttribute("verifyCheck");
	    memberDTO.setMemEmail((String) session.getAttribute("verifyEmail"));
	    if (verifyCheck == null || !verifyCheck) {
	        model.addAttribute("wrongMessageEmail", "請先完成信箱驗證");
	        count++;
	    }
	        	
	    // 密碼再次輸入檢查
	    if (memberDTO.getConfirmPassword().isEmpty()) {
	        model.addAttribute("wrongMessage", "請再次輸入密碼");
	        count++;
	    }
	    if (!memberDTO.getMemPassword().equals(memberDTO.getConfirmPassword())) {
	        model.addAttribute("wrongMessage", "與密碼不相同");
	        count++;
	    }
	    System.out.println(memberDTO.getMemPhone());
	    if (memberService.findMemberByPhone(memberDTO.getMemPhone())) {
	        model.addAttribute("wrongMessagePhone", "此號碼已註冊");
	        count++;
	    }

	    // DTO 欄位驗證錯誤處理
	    if (bindingResult.hasErrors()) {
	        model.addAttribute("memberDTO", memberDTO);
	        if (bindingResult.hasErrors()) {
//	            bindingResult.getAllErrors().forEach(err -> System.out.println(err.getDefaultMessage()));
	            model.addAttribute("memberDTO", memberDTO);
	            count++;
	        }
	        count++;
	    }
	   
	    if (count ==0) {
	    	 // 存入會員
	    memberService.saveMember(memberDTO, session);
	    session.setAttribute("isLoggedIn", true);

	    // 註冊後清除驗證用 session（避免亂用）
	    session.removeAttribute("verifyEmail");
	    session.removeAttribute("verifyCode");
	    session.removeAttribute("verifyCheck");

	    return "redirect:/";
	    	
	    }
	   
	    
	    return "/front-end/register";
	    
	    
	}
	
	@GetMapping("/updateMember")
	public String updateMember() {
		return "/front-end/updateMember";
	}
	
	@GetMapping("/passwordUpdate")
	public String passwordUpdate() {
		return "/front-end/passwordUpdate";
	}

}
