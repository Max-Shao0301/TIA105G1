package com.orders.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;
import com.orders.model.OrdersService;
import com.orders.model.dto.AppointmentTimeDTO;
import com.orders.model.dto.CheckoutOrderDTO;
import com.schedule.model.ScheduleService;
import com.schedule.model.dto.StaffScheduleDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	private MemberService memberService;
	
	@GetMapping("/appointment/getbookableStaff")
	public ResponseEntity<List<StaffScheduleDTO>> getBookableStaff(@Valid @ModelAttribute AppointmentTimeDTO apptDTO) {

		List<StaffScheduleDTO> staffScheduleList = scheduleService.findByBookableStaff(apptDTO.getDate(),
				apptDTO.getApptTime());

		return ResponseEntity.ok(staffScheduleList);
	}
	
	@PostMapping("/appointment/getMemInfo")
	public ResponseEntity<MemberDTO> getMemInfo(@RequestBody Map<String, Integer> reqBody, HttpSession session) {
		Integer memId = reqBody.get("memId");

		session.setAttribute("memId", memId); // 模擬登入成功
		MemberDTO memberDTO = memberService.getMemberDTO(memId);
		

		return ResponseEntity.ok(memberDTO);
	}
	
	//要將訂單資料整理好後存進DB 再拿存進DB的訂單編號發給ECPaySDK生成網頁，傳給前端 理論上這邊會呼叫兩個方法1.存訂單資料給DB 2.發資料給ECPay
	@PostMapping("/appointment/postCheckout") 
	public ResponseEntity<String>postCheckout(@RequestBody CheckoutOrderDTO checkoutOrderDTO){
		
		String form = ordersService.addOrders(checkoutOrderDTO);
		
		
		return ResponseEntity.ok(form);
	}

}
