package com.member.controller;

import com.member.model.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
	public String register() {
		return "/front-end/register";
	}

}
