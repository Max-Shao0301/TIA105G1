package com;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.MemberService;
import com.weather.WeatherResponse;
import com.weather.WeatherService;

import jakarta.servlet.http.HttpSession;

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