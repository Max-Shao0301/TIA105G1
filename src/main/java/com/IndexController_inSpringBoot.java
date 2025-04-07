package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;

@Controller
public class IndexController_inSpringBoot {
	@Autowired
	private MemberService memberService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		boolean isLoggedIn;
		if (session.getAttribute("isLoggedIn") != null) {
			isLoggedIn = (boolean) session.getAttribute("isLoggedIn");
			model.addAttribute("memName", session.getAttribute("memName"));
		} else {
			isLoggedIn = false;
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		return "index"; // 回傳 Thymeleaf 頁面
	}

	
}