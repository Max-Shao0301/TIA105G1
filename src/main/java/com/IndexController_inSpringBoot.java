package com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController_inSpringBoot {
	@Autowired
	private MemberService memberService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		boolean isLoggedIn;
		if (session.getAttribute("isLoggedIn") != null) {
			isLoggedIn = (boolean) session.getAttribute("isLoggedIn");
			System.out.println(session.getAttribute("memName"));
			model.addAttribute("memName", session.getAttribute("memName"));
		} else {
			isLoggedIn = false;
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		return "index"; // 回傳 Thymeleaf 頁面
	}

	@GetMapping("/front-end/login")
	public String loginPage() {
		return "/front-end/login";
	}

	@PostMapping("/logincheck")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
		Boolean isLoggedIn = memberService.findMember(email, password, session);
		session.setAttribute("isLoggedIn", isLoggedIn);
//		System.out.println(isLoggedIn);
		return "redirect:/";
	}

	@GetMapping("/front-end/member")
	public String member() {
		return "/front-end/member";
	}

	@GetMapping("/front-end/joinus")
	public String joinus() {
		return "/front-end/joinus";
	}

	@GetMapping("/front-end/orderlist")
	public String orderlist() {
		return "/front-end/orderlist";
	}

	@GetMapping("/front-end/appointment")
	public String appointment() {
		return "/front-end/appointment";
	}

}
