package com.member.model;

import java.lang.reflect.Member;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.springbootmail.MailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.member.model.dto.MemberDTO;
import com.member.model.dto.UpdateMemberDTO;
import com.orders.model.OrdersVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service("memberService")
public class MemberService {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	// 新增會員
	@Transactional
	public MemberVO saveMember(MemberDTO memberDTO, HttpSession session) {
		MemberVO member = new MemberVO();
		String city = memberDTO.getCity().equals("Taipei") ? "台北市" : "新北市";
		String memAddress = city + memberDTO.getDistrict() + memberDTO.getAddress();
		member.setMemEmail(memberDTO.getMemEmail());
		member.setMemName(memberDTO.getMemName());
		member.setMemPhone(memberDTO.getMemPhone());
//		member.setMemPassword(passwordEncoder.encode(memberDTO.getMemPassword())); //雜湊密碼加密
		member.setMemPassword(memberDTO.getMemPassword());//明碼保存
		member.setAddress(memAddress);
		memberRepository.save(member);
		session.setAttribute("memId", member.getMemId());
		session.setAttribute("memName", member.getMemName());
		return member;
	}

	// 更新會員
	@Transactional
	public void updateMember(MemberVO memberVO) {
		memberRepository.save(memberVO);
	}

	public MemberVO getOneMember(Integer memId) {
		Optional<MemberVO> optional = memberRepository.findById(memId);
		return optional.orElse(null); // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<MemberVO> getAll() {
		return memberRepository.findAll();// 方法都不是自己寫的!(要先測試!)
	}

	// 更新會員狀態
	@Transactional
	public void updateStatus(Integer statusnum, Integer memId) {
		memberRepository.updateStatus(statusnum, memId);
	}

	// 更新會員狀態admin用
	@Transactional
	public void deleteMember(Integer memId) {
		String title = "寵愛牠會員停權通知";
		MemberVO memberVO = getOneMember(memId);
		if (memberVO != null) {
			memberVO.setStatus(0);
			mailService.sendPlainText(Collections.singleton(memberVO.getMemEmail()), title, memberVO.getMemName()
					+ "先生/小姐，您好\n因您多次違反平台使用規則，將對您的帳號進行停權處理，即刻起無法登入系統\n，如您對此決定有任何疑問，或認為此處分有誤，請聯繫我們，並提供相關證明進行申訴\n聯繫信箱： max.shao@icloud.com\n感謝您的配合與理解\n寵愛牠平台方客服");
			memberRepository.save(memberVO);
		}
	}

	// 查詢某會員所有訂單
	public List<OrdersVO> getOrdersByMemberId(Integer memId) {
		MemberVO member = memberRepository.findById(memId).orElseThrow(() -> new RuntimeException("找不到該會員"));
		return member.getOrders();
	}

	// 查詢某信箱是否為會員
	public Boolean findMember(String memEmail) {
		MemberVO member = memberRepository.findByMemEmail(memEmail);
		return member != null; // false表示無註冊過

	}

	// 登入會員
	public Integer checkMember(String memEmail, String memPassword, HttpSession session) {
		MemberVO member = memberRepository.findByMemEmail(memEmail);
		if (member == null) {
			return 2;
		}
		// 雜湊密碼加密比對
		if (passwordEncoder.matches(memPassword, member.getMemPassword())) {
			session.setAttribute("memId", member.getMemId());
			session.setAttribute("memName", member.getMemName());
			return 1;
		} else {
			return 3;
		}

		// 明碼比對
//		if (memPassword.equals(member.getMemPassword())) {
//			session.setAttribute("memId", member.getMemId());
//			session.setAttribute("memName", member.getMemName());
//			return 1;
//		} else {
//			return 3;
//		}
	}

	public MemberDTO getMemberDTO(Integer memId) {
		MemberVO memberVO = memberRepository.findById(memId).orElse(null);
		MemberDTO memberDTO = new MemberDTO();

		memberDTO.setMemId(memberVO.getMemId());
		memberDTO.setMemName(memberVO.getMemName());
		memberDTO.setMemPhone(memberVO.getMemPhone());
		memberDTO.setPoint(memberVO.getPoint());

		return memberDTO;
	}

	// 查詢某手機號碼是否已註冊過
	public Integer findMemberByPhone(String memPhone) {
		MemberVO member = memberRepository.findByMemPhone(memPhone);
		if (member == null) {
			return null;
		}
		return member.getMemId(); // false表示無註冊過
	}

	// 更新密碼
	public void updatePassword(String memEmail, String memPassword) {
		MemberVO member = memberRepository.findByMemEmail(memEmail);
		member.setMemPassword(passwordEncoder.encode(memPassword));//雜湊密碼加密
//		member.setMemPassword(memPassword);//明碼保存
		updateMember(member);
	}

	public void updatePassword(Integer memId, String memPassword) {
		MemberVO member = getOneMember(memId);
		member.setMemPassword(passwordEncoder.encode(memPassword));//雜湊密碼加密
//		member.setMemPassword(memPassword);	//明碼保存
		updateMember(member);
	}

	// 更新會員資料
	public void updateMemberData(Integer memId, UpdateMemberDTO updateMemberDTO) {
		MemberVO member = getOneMember(memId);
		String city = updateMemberDTO.getCity().equals("Taipei") ? "台北市" : "新北市";
		String memAddress = city + updateMemberDTO.getDistrict() + updateMemberDTO.getAddress();
		member.setMemName(updateMemberDTO.getMemName());
		member.setMemPhone(updateMemberDTO.getMemPhone());
		member.setAddress(memAddress);
		updateMember(member);
	}

	// 分割地址
	public String[] separateAddress(String fullAddress) {
		String city;

		if (fullAddress == null) {
			String[] noAddress = { "請選擇縣市", "請選擇區域", "" };
			return noAddress;
		} else {
			if (fullAddress.contains("台北市")) {
				city = "Taipei";
			} else {
				city = "NewTaipei";
			}

			String district = fullAddress.substring(3, 6);
			String address = fullAddress.substring(6);
			String[] separate = { city, district, address };
			return separate;
		}

	}

	@Transactional
	public MemberVO saveOAuth2Member(String memEmail, String memName, HttpSession session) {
		MemberVO existingMember = memberRepository.findByMemEmail(memEmail);
		// 如果會員已存在，則不需要註冊，用現有資料登入
		if (existingMember != null) {
			session.setAttribute("memId", existingMember.getMemId());
			session.setAttribute("memName", existingMember.getMemName());
			session.setAttribute("isLoggedIn", true);// 首頁登入判斷
			return existingMember;
		}

		MemberVO memberVO = new MemberVO();
		memberVO.setMemEmail(memEmail);
		memberVO.setMemName(memName);

		// 產生隨機密碼
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		int length = 10;
		StringBuilder result = new StringBuilder();
		Random random = new Random();
		String code ="";

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(characters.length());
			code = result.append(characters.charAt(index)).toString();
		}

		memberVO.setMemPassword(passwordEncoder.encode(code));// 隨機密碼加密

		MemberVO save = memberRepository.save(memberVO);

		session.setAttribute("memId", memberVO.getMemId());
		session.setAttribute("memName", memberVO.getMemName());
		session.setAttribute("isLoggedIn", true);// 首頁登入判斷

		mailService.sendPlainText(Collections.singleton(memEmail), "寵愛牠會員註冊通知",
				"親愛的 " + memName + " 您好，感謝您使用第三方登入註冊寵愛牠平台\n未來您可選擇繼續使用第三方登入或者在官網輸入下列個人資訊登入\n您的帳號為：" + memEmail + "\n您的預設密碼為：" + code + "\n請妥善保管，謝謝");

		return save;

	}


}