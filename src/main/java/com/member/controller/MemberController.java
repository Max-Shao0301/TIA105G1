package com.member.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String register(Model model) {
		model.addAttribute("memberDTO", new MemberDTO());
		return "/front-end/register";
	}
	
	@PostMapping("/register")
	public String register(@ModelAttribute @Valid MemberDTO memberDTO,BindingResult bindingResult, HttpSession session,Model model){
		Integer error = 0;
		if (memberDTO.getConfirmPassword().isEmpty()) {
			model.addAttribute("wrongMessage", "請再次輸入密碼");
			error++;
		}else if (!memberDTO.getMemPassword().equals(memberDTO.getConfirmPassword())) {
			model.addAttribute("wrongMessage", "與密碼不相同");
			error++;
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("memberDTO", memberDTO);
			error++;
		}
		
		if(error == 0) {
			memberService.saveMember(memberDTO, session);
			session.setAttribute("isLoggedIn", true);
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
