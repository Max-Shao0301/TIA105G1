package com.member.controller;

import com.member.model.MemberService;
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
    
	@GetMapping("/front-end/forgetpassword")
	public String forgetpassword() {
		return "/front-end/forgetpassword";
	}
    
	@GetMapping("/front-end/register")
	public String register(Model model) {
		model.addAttribute("memberDTO", new MemberDTO());
		return "/front-end/register";
	}
	
	@PostMapping("/front-end/register")
	public String register(@ModelAttribute @Valid MemberDTO memberDTO,BindingResult bindingResult, HttpSession session,Model model){
		if (bindingResult.hasErrors()) {
			model.addAttribute("memberDTO", memberDTO);
			return "/front-end/register";
		}
		memberService.saveMember(memberDTO, session);
		session.setAttribute("isLoggedIn", true);
		return "redirect:/";
	}
	

}
