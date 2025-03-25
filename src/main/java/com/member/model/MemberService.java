package com.member.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orders.model.OrdersVO;


@Service("memberService")
public class MemberService {
	
	@Autowired
	MemberRepository memberRepository;
	
	//新增會員
	public void  addMember(MemberVO memberVO) {
		memberRepository.save(memberVO);
	}
	
	//更新會員
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
	public void updateStatus(Integer statusnum, Integer memId) {
		memberRepository.updateStatus(statusnum,memId);
	}
	
	//查詢某會員所有訂單
	public List<OrdersVO> getOrdersByMemberId(Integer memId) {
	    MemberVO member = memberRepository.findById(memId)
	        .orElseThrow(() -> new RuntimeException("找不到該會員"));
	    return member.getOrders();
	}
		
}