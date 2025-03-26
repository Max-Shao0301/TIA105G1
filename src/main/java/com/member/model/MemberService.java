package com.member.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.springbootmail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orders.model.OrdersVO;
import org.springframework.transaction.annotation.Transactional;


@Service("memberService")
public class MemberService {
	
	@Autowired
	MemberRepository memberRepository;

	@Autowired
	private MailService mailService;
	
	//新增會員
	@Transactional
	public void  addMember(MemberVO memberVO) {
		memberRepository.save(memberVO);
	}
	
	//更新會員
	@Transactional
	public void updateMember(MemberVO memberVO) {
		memberRepository.save(memberVO);
	}
	
	public MemberVO getOneMember(Integer memId) {
		Optional<MemberVO> optional = memberRepository.findById(memId);
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}
	
	public List<MemberVO> getAll() {
		return memberRepository.findAll();//方法都不是自己寫的!(要先測試!)
	}
	
	//更新會員狀態
	@Transactional
	public void updateStatus(Integer statusnum, Integer memId) {
		memberRepository.updateStatus(statusnum,memId);
	}
	//更新會員狀態admin用
	@Transactional
	public void deleteMember(Integer memId) {
		String title = "寵愛牠會員停權通知";
		MemberVO memberVO = getOneMember(memId);
		if (memberVO != null) {
			memberVO.setStatus(0);
			mailService.sendPlainText(Collections.singleton(memberVO.getMemEmail()),
					title,
					memberVO.getMemName() + "先生/小姐，您好\n因您多次違反平台使用規則，將對您的帳號進行停權處理，即刻起無法登入系統\n，如您對此決定有任何疑問，或認為此處分有誤，請聯繫我們，並提供相關證明進行申訴\n聯繫信箱： max.shao@icloud.com\n感謝您的配合與理解\n寵愛牠平台方客服"
			);
			memberRepository.save(memberVO);
		}
	}


	//查詢某會員所有訂單
	public List<OrdersVO> getOrdersByMemberId(Integer memId) {
	    MemberVO member = memberRepository.findById(memId)
	        .orElseThrow(() -> new RuntimeException("找不到該會員"));
	    return member.getOrders();
	}
		
}