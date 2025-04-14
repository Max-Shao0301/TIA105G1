package com.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.ResetPasswordDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
public class ResetPasswordController {

	@Autowired
	private MemberService memberService;
    @Autowired
    private PasswordEncoder passwordEncoder;

	@GetMapping("/forgetPassword")
	public String forgetPassword() {
		return "/front-end/forgetPassword";
	}

	// 登入時的忘記密碼 
	@GetMapping("/resetPassword")
	public String showResetPasswordPage(HttpSession session, Model model) {
		Boolean verified = (Boolean) session.getAttribute("verifyCheck");
		if (verified == null || !verified) {
			return "redirect:/forgetPassword"; // 還沒驗證 → 導回驗證頁
		}

		if (!model.containsAttribute("resetPasswordDTO")) {
			model.addAttribute("resetPasswordDTO", new ResetPasswordDTO());
		}

		return "/front-end/resetPassword";
	}
	// 登入時的忘記密碼 密碼重設
	@PostMapping("/resetPassword")
	public String resetPassword(@ModelAttribute("resetPasswordDTO") @Validated ResetPasswordDTO resetPasswordDTO,
			BindingResult bindingResult, HttpSession session, Model model) {
		if (bindingResult.hasErrors()) {
			return "/front-end/resetPassword";
		}

		session.removeAttribute("errorMessage");
		String password = resetPasswordDTO.getPassword();
		if (!password.equals(resetPasswordDTO.getConfirmPassword())) {
			model.addAttribute("errorMessage", "與新密碼輸入不一致");
			return "/front-end/resetPassword";
		}
		memberService.updatePassword((String) session.getAttribute("verifyEmail"), password);
		session.removeAttribute("verifyCheck");
		session.removeAttribute("verifyCode");
		session.removeAttribute("verifyEmail");
		return "redirect:/login";

	}

	// 會員資料的重設密碼
	@PostMapping("/member/passwordUpdate")
	public String passwordUpdate(@ModelAttribute("resetPasswordDTO") @Validated ResetPasswordDTO resetPasswordDTO,
			BindingResult bindingResult, HttpSession session,
			Model model) {
		Integer memId = (Integer) session.getAttribute("memId");
		MemberVO memberVO = memberService.getOneMember(memId); 
		String oldPassword = resetPasswordDTO.getOldPassword();
		if (oldPassword == null || oldPassword.isEmpty()) {
	        model.addAttribute("errorOldPassword", "請填入舊密碼");
	        return "/front-end/passwordUpdate";
	    }

		if (!passwordEncoder.matches(oldPassword, memberVO.getMemPassword())) { //雜湊密碼比對
			model.addAttribute("errorOldPassword", "舊密碼錯誤");
			return "/front-end/passwordUpdate";
		}

//		if (!oldPassword.equals(memberVO.getMemPassword())){ //明碼比對
//			model.addAttribute("errorOldPassword", "舊密碼錯誤");
//			return "/front-end/passwordUpdate";
//		}
		
		if (bindingResult.hasErrors()) {
			return "/front-end/passwordUpdate";
		}

		String password = resetPasswordDTO.getPassword();
		if (!password.equals(resetPasswordDTO.getConfirmPassword())) {
			model.addAttribute("errorMessage", "與新密碼輸入不一致");
			model.addAttribute("resetPasswordDTO", resetPasswordDTO);
			return "/front-end/passwordUpdate";
		}
		memberService.updatePassword(memId, password);
		session.setAttribute("passwordUpdated", true);
		return "redirect:/member/passwordUpdate";
	}
	// 會員資料的重設密碼頁面
	@GetMapping("/member/passwordUpdate")
	public String passwordUpdate(Model model, HttpSession session) {
		if (!model.containsAttribute("resetPasswordDTO")) {
			model.addAttribute("resetPasswordDTO", new ResetPasswordDTO());
		}
		Boolean updated = (Boolean) session.getAttribute("passwordUpdated");
		if (updated != null && updated) {
		    model.addAttribute("passwordUpdated", true);
		    session.removeAttribute("passwordUpdated");
		}
		
		return "/front-end/passwordUpdate";
	}
}
