package com.member.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.member.model.dto.MemberDTO;
import com.member.model.dto.OrderMemberInfoDTO;
import com.member.model.dto.UpdateMemberDTO;
import com.orders.model.OrdersVO;
import com.springbootmail.MailService;

import jakarta.servlet.http.HttpSession;

@Service("memberService")
public class MemberService {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MailService mailService;

	@Value("${recaptcha.secret}")
	private String recaptchaSecret;

	@Value("${recaptcha.sitekey}")
	private String siteKey;

	public String getSiteKey() {
		return siteKey;
	}

	// 新增會員
	@Transactional
	public MemberVO saveMember(MemberDTO memberDTO, HttpSession session) {
		MemberVO member = new MemberVO();
		String city = memberDTO.getCity().equals("Taipei") ? "台北市" : "新北市";
		String memAddress = city + memberDTO.getDistrict() + memberDTO.getAddress();
		member.setMemEmail(memberDTO.getMemEmail());
		member.setMemName(memberDTO.getMemName());
		member.setMemPhone(memberDTO.getMemPhone());
		member.setMemPassword(passwordEncoder.encode(memberDTO.getMemPassword())); // 雜湊密碼加密
//		member.setMemPassword(memberDTO.getMemPassword());//明碼保存
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

		if ((Integer) member.getStatus() == 0) {
			return 4;
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

	public MemberDTO getMemberDTO(HttpSession session) {
		Integer memId = (Integer) session.getAttribute("memId");
		MemberVO memberVO = memberRepository.findById(memId).orElse(null);
		MemberDTO memberDTO = new MemberDTO();

		memberDTO.setMemId(memberVO.getMemId());
		memberDTO.setMemName(memberVO.getMemName());
		memberDTO.setMemPhone(memberVO.getMemPhone());
		memberDTO.setPoint(memberVO.getPoint());

		return memberDTO;
	}

	public OrderMemberInfoDTO getOrderMemberInfoDTO(HttpSession session) {
		Integer memId = (Integer) session.getAttribute("memId");
		MemberVO memberVO = memberRepository.findById(memId).orElse(null);
		OrderMemberInfoDTO orderMemberInfoDTO = new OrderMemberInfoDTO();

		orderMemberInfoDTO.setMemId(memId);
		orderMemberInfoDTO.setAddress(memberVO.getAddress());
		orderMemberInfoDTO.setMemEmail(memberVO.getMemEmail());
		orderMemberInfoDTO.setMemName(memberVO.getMemName());
		orderMemberInfoDTO.setMemPhone(memberVO.getMemPhone());
		orderMemberInfoDTO.setPoint(memberVO.getPoint());

		return orderMemberInfoDTO;
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
//		System.out.println(member.getMemEmail());
		member.setMemPassword(passwordEncoder.encode(memPassword));// 雜湊密碼加密
//		System.out.println(passwordEncoder.encode(memPassword));
//		member.setMemPassword(memPassword);//明碼保存
		updateMember(member);
	}

	public void updatePassword(Integer memId, String memPassword) {
		MemberVO member = getOneMember(memId);
		member.setMemPassword(passwordEncoder.encode(memPassword));// 雜湊密碼加密
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

	// 透過第三方登入註冊會員
	@Transactional
	public MemberVO saveOAuth2Member(String memEmail, String memName, HttpSession session) {
		MemberVO existingMember = memberRepository.findByMemEmail(memEmail);
		// 如果會員已存在並且啟用狀態為啟用，則不需要註冊，用現有資料登入
		if (existingMember != null && existingMember.getStatus() == 1) {
			session.setAttribute("memId", existingMember.getMemId());
			session.setAttribute("memName", existingMember.getMemName());
			session.setAttribute("isLoggedIn", true);// 首頁登入判斷
			return existingMember;
		} else if (existingMember != null && existingMember.getStatus() == 0) {
			session.setAttribute("memId", existingMember.getMemId());
			session.setAttribute("memName", existingMember.getMemName());
			session.setAttribute("isLoggedIn", false);// 首頁登入判斷
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
		String code = "";

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
				"親愛的 " + memName + " 您好，感謝您使用第三方登入註冊寵愛牠平台\n未來您可選擇繼續使用第三方登入或者在官網輸入下列個人資訊登入\n您的帳號為：" + memEmail
						+ "\n您的預設密碼為：" + code + "\n請妥善保管，謝謝");

		return save;

	}

	// reCAPTCHA
	public boolean verifyCaptcha(String responseToken) {
		String secret = recaptchaSecret;
		String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

		RestTemplate restTemplate = new RestTemplate(); // RestTemplate送 application/x-www-form-urlencoded 格式(Google
														// 要求的參數格式)
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // 會自動轉成表單格式
		params.add("secret", secret);
		params.add("response", responseToken);

		ResponseEntity<Map> response = restTemplate.postForEntity(verifyUrl, params, Map.class);
		Map body = response.getBody();

		return (Boolean) body.get("success");
//		API Response
//		{
//		  "success": true|false,
//		  "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
//		  "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
//		  "error-codes": [...]        // optional
//		}
	}

	public boolean infoIsComplete(MemberVO memberVO) {
		return memberVO.getMemPhone() != null && memberVO.getAddress() != null && memberVO.getMemEmail() != null
				&& memberVO.getMemName() != null;
	}
}