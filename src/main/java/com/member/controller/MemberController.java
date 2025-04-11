package com.member.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;
import com.member.model.dto.ResetPasswordDTO;
import com.member.model.dto.UpdateMemberDTO;
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
	public String updateMemberStatus(@RequestParam("member_id") Integer memId) {
		memberService.deleteMember(memId);

		return "redirect:/admin/home/page";
	}

	@GetMapping("/member")
	public String member(HttpSession session, Model model) {
		Integer memId = (Integer) session.getAttribute("memId");
//		System.out.println(memId);
		MemberVO memberVO = memberService.getOneMember(memId);
		model.addAttribute("memberVO", memberVO);
		return "/front-end/member";
	}

	// 註冊 - 信箱驗證
	@GetMapping("/verifyEmail")
	public String verifyEmail() {
		return "/front-end/verifyEmail";
	}

	// 註冊 - 更換信箱
	@GetMapping("/changeEmail")
	public String changeEmail(HttpSession session) {
		session.removeAttribute("verifyCheck");
		session.removeAttribute("verifyEmail");
		return "/front-end/verifyEmail";
	}

	// 註冊 - 到註冊頁
	@GetMapping("/registerMember")
	public String register(Model model, HttpSession session) {
		Boolean verifyCheck = (Boolean)session.getAttribute("verifyCheck");
		if(verifyCheck != null && verifyCheck) {
		model.addAttribute("memberDTO", new MemberDTO());
		return "/front-end/registerMember";
		}
		return "/front-end/verifyEmail";
	}

	// 註冊 - 增加會員資料
	@PostMapping("/registerMember")
	public String register(@ModelAttribute @Valid MemberDTO memberDTO, BindingResult bindingResult, HttpSession session,
			Model model) {
		Integer count = 0;

		memberDTO.setMemEmail((String) session.getAttribute("verifyEmail"));

		// 密碼再次輸入檢查
		if (memberDTO.getConfirmPassword().isEmpty()) {
			model.addAttribute("wrongMessage", "請再次輸入密碼");
			count++;
		}
		if (!memberDTO.getMemPassword().equals(memberDTO.getConfirmPassword())) {
			model.addAttribute("wrongMessage", "與密碼不相同");
			count++;
		}
//		System.out.println(memberDTO.getMemPhone());
		if (memberService.findMemberByPhone(memberDTO.getMemPhone()) != null) {
			model.addAttribute("wrongMessagePhone", "此號碼已註冊");
			count++;
		}

		// DTO 欄位驗證錯誤處理
		if (bindingResult.hasErrors()) {
			model.addAttribute("memberDTO", memberDTO);
			count++;
		}

		if (count == 0) {
			// 存入會員
			memberService.saveMember(memberDTO, session);
			session.setAttribute("isLoggedIn", true);

			// 註冊後清除驗證用 session（避免亂用）
			session.removeAttribute("verifyEmail");
			session.removeAttribute("verifyCode");
			session.removeAttribute("verifyCheck");

			return "redirect:/";

		}

		return "/front-end/registerMember";

	}

	// 更新會員資料 - 到更新頁面
	@GetMapping("/updateMember")
	public String updateMember(Model model, HttpSession session) {
	    Integer memId = (Integer) session.getAttribute("memId");
	    MemberVO member = memberService.getOneMember(memId);
	    UpdateMemberDTO dto = new UpdateMemberDTO();
	    dto.setMemName(member.getMemName());
	    dto.setMemPhone(member.getMemPhone());

	    String fullAddress = member.getAddress();
	    dto.setCity(memberService.separateAddress(fullAddress)[0]);
	    dto.setDistrict(memberService.separateAddress(fullAddress)[1]);
	    dto.setAddress(memberService.separateAddress(fullAddress)[2]);

	    model.addAttribute("updateMemberDTO", dto);

	    // 顯示修改成功的彈窗
	    Boolean memberUpdated = (Boolean) session.getAttribute("memberUpdated");
	    if (memberUpdated != null && memberUpdated) {
	        model.addAttribute("memberUpdated", true);
	        session.removeAttribute("memberUpdated");
	    }

	    return "/front-end/updateMember";
	}

	// 更新會員資料
	@PostMapping("/updateMember")
	public String updateMemberdata(@ModelAttribute @Valid UpdateMemberDTO updateMemberDTO, BindingResult bindingResult,
			HttpSession session, Model model) {

		Integer count = 0;
		Integer memId = (Integer) session.getAttribute("memId");
		Integer registerPhoneId = memberService.findMemberByPhone(updateMemberDTO.getMemPhone());
		System.out.println(memId);
		System.out.println(registerPhoneId);
		
		if (registerPhoneId != null && !registerPhoneId.equals(memId)) {
			model.addAttribute("wrongMessagePhone", "此號碼已綁定其他帳號");
			count++;
		}

		if (bindingResult.hasErrors()) {
			model.addAttribute("updateMemberDTO", updateMemberDTO);
			count++;

		}

		if (count != 0) {
			return "/front-end/updateMember";
		}

		
		memberService.updateMemberData(memId, updateMemberDTO);
		session.setAttribute("memName", updateMemberDTO.getMemName());
		session.setAttribute("memberUpdated", true);
		return "redirect:/updateMember";
	}

}
