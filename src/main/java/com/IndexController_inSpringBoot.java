package com;

import com.weather.WeatherResponse;
import com.weather.WeatherService;
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

	@Autowired
	private WeatherService weatherService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		boolean isLoggedIn;
		if (session.getAttribute("isLoggedIn") != null) {
			isLoggedIn = (boolean) session.getAttribute("isLoggedIn");
			model.addAttribute("memName", session.getAttribute("memName"));
		} else {
			isLoggedIn = false;
		}
		//取得天氣資訊
		WeatherResponse taipeiWeather = weatherService.getWeather("Taipei");
		model.addAttribute("weather", taipeiWeather);
		model.addAttribute("isLoggedIn", isLoggedIn);
		session.setAttribute("isLoggedIn", isLoggedIn);
		return "index"; // 回傳 Thymeleaf 頁面
	}

	@GetMapping("/aboutUs")
	public String aboutUs(Model model, HttpSession session) {
		boolean isLoggedIn;
		if (session.getAttribute("isLoggedIn") != null) {
			isLoggedIn = (boolean) session.getAttribute("isLoggedIn");
			model.addAttribute("memName", session.getAttribute("memName"));
		} else {
			isLoggedIn = false;
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		session.setAttribute("isLoggedIn", isLoggedIn);
		System.out.println();
		return "/front-end/aboutUs";
	}

	@GetMapping("/questionAndAnswer")
	public String questionAndAnswer(Model model, HttpSession session) {
		boolean isLoggedIn;
		if (session.getAttribute("isLoggedIn") != null) {
			isLoggedIn = (boolean) session.getAttribute("isLoggedIn");
			model.addAttribute("memName", session.getAttribute("memName"));
		} else {
			isLoggedIn = false;
		}
		model.addAttribute("isLoggedIn", isLoggedIn);
		session.setAttribute("isLoggedIn", isLoggedIn);
		System.out.println();
		return "/front-end/questionAndAnswer";
	}

}