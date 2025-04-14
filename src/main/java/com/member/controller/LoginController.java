package com.member.controller;

import com.member.model.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.member.model.MemberService;
import com.member.model.dto.LoginDTO;
import com.member.model.dto.MemberDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Controller
public class LoginController{
	@Autowired
	private MemberService memberService;
	
	
	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request, HttpSession session) {
	    LoginDTO loginDTO = (LoginDTO) model.getAttribute("loginDTO");
	    boolean rememberMeChecked = false;

	    if (loginDTO == null) {
	        loginDTO = new LoginDTO();

	        // 只有在 loginDTO == null 時才載入 cookie
	        if (request.getCookies() != null) {
	            for (Cookie cookie : request.getCookies()) {
	                if ("rememberEmail".equals(cookie.getName())) {
	                    loginDTO.setLoginEmail(cookie.getValue());
	                    rememberMeChecked = true;
	                }
	            }
	        }
	    }
	    
	    String error = (String) session.getAttribute("loginError");
	    if (error != null) {
	        model.addAttribute("errorMessage", error);
	        session.removeAttribute("loginError"); // 拿完就清掉
	    }

	    model.addAttribute("loginDTO", loginDTO);
	    model.addAttribute("rememberMeChecked", rememberMeChecked);
	    return "/front-end/login";
	}
	
	@Validated
	@PostMapping("/login")
	public String login(
			@ModelAttribute @Valid LoginDTO loginDTO,
			BindingResult bindingResult,
			HttpSession session,
			HttpServletResponse response,
	        @RequestParam(value = "rememberMe", required = false) String rememberMe,
		    RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("loginDTO", loginDTO);
			return "/front-end/login";
		}
		Integer login = memberService.checkMember(loginDTO.getLoginEmail(),loginDTO.getLoginPassword(),session);
		switch (login) {
		case 1: 
			 if (rememberMe != null) {
                 Cookie cookie = new Cookie("rememberEmail", loginDTO.getLoginEmail());
                 cookie.setMaxAge(7 * 24 * 60 * 60); // 7 天
                 cookie.setPath("/");
                 response.addCookie(cookie);
             } else {
                 Cookie cookie = new Cookie("rememberEmail", null);
                 cookie.setMaxAge(0);
                 cookie.setPath("/");
                 response.addCookie(cookie);
             }
			 session.setAttribute("isLoggedIn", true);
			 model.addAttribute("isLoggedIn", true);
			//如果有設定二階段登入驗證，則轉向到二階段登入頁面
			Integer memId = (Integer) session.getAttribute("memId");
			MemberVO memberVO = memberService.getOneMember(memId);
			if (memberVO.getSecret() != null && !memberVO.getSecret().isEmpty()) {
				return "redirect:/mfaLoginPage";
			} else {
				return "redirect:/";
			}

        case 2:
        	redirectAttributes.addFlashAttribute("errorMessage1","此信箱未註冊");
        	redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
        	return "redirect:/login";
        case 3: 
        	redirectAttributes.addFlashAttribute("errorMessage2","密碼錯誤");
        	redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
        	return "redirect:/login";
        case 4:
        	redirectAttributes.addFlashAttribute("errorMessage1","此帳號已停權，請聯絡客服或換帳號");
        	redirectAttributes.addFlashAttribute("loginDTO", loginDTO);
        	return "redirect:/login";
        default: return "redirect:/login";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}
