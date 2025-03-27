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
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.member.model.dto.MemberDTO;
import com.orders.model.OrdersService;
import com.orders.model.dto.AppointmentTimeDTO;
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
	
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	@GetMapping("/appointment/getbookableStaff")
	public ResponseEntity<List<StaffScheduleDTO>> getBookableStaff(@Valid @ModelAttribute AppointmentTimeDTO apptDTO) {

		List<StaffScheduleDTO> staffScheduleList = scheduleService.findByBookableStaff(apptDTO.getDate(),
				apptDTO.getApptTime());

		return ResponseEntity.ok(staffScheduleList);
	}
	
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	@PostMapping("/appointment/getMemInfo")
	public ResponseEntity<MemberDTO> getMemInfo(@RequestBody Map<String, Integer> reqBody, HttpSession session) {
		Integer memId = reqBody.get("memId");
		System.out.println(memId);
		session.setAttribute("memId", memId); // 模擬登入成功
		System.out.println(session.getAttribute("memId"));
		System.out.println("Session ID: " + session.getId());
		MemberVO memberVO = memberService.getOneMember(memId);
		MemberDTO memberDTO = new MemberDTO();
		memberDTO.setMemId(memberVO.getMemId());
//		memberDTO.setMemEmail(memberVO.getMemEmail());
		memberDTO.setMemName(memberVO.getMemName());
		memberDTO.setMemPhone(memberVO.getMemPhone());
		memberDTO.setPoint(memberVO.getPoint());

		return ResponseEntity.ok(memberDTO);
	}

}
