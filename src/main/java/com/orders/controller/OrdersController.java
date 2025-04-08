package com.orders.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ecpay.payment.integration.AllInOne;
import com.member.model.MemberService;
import com.member.model.dto.MemberDTO;
import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;
import com.orders.model.dto.AppointmentTimeDTO;
import com.orders.model.dto.CheckoutOrderDTO;
import com.orders.model.dto.OrderDetailDTO;
import com.orders.model.dto.OrderViewDTO;
import com.orders.model.dto.CommentDTO;
import com.schedule.model.ScheduleService;
import com.schedule.model.dto.StaffScheduleDTO;

import jakarta.servlet.http.HttpServletRequest;
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

	@GetMapping("/appointment/paymentResults")
	public String paymentResults() {
		return "/front-end/paymentResults";
	}

	@PostMapping("/appointment/postCheckout")
	public ResponseEntity<Map<String, Object>> postCheckout(@Valid @RequestBody CheckoutOrderDTO checkoutOrderDTO,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		Integer memId = (Integer) session.getAttribute("memId");
		Map result = ordersService.addOrders(checkoutOrderDTO, memId);
		session.setAttribute("orderId", result.get("orderId"));
		
		if (result.get("error") != null) {
			response.put("error", result.get("error"));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		//免費訂單
		if ("true".equals(result.get("freeOrder"))) {
			response.put("NoPayment", "http://localhost:8080/appointment/paymentResults");
			return ResponseEntity.ok(response);
		}
		response.put("ECPay", result.get("form"));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/ecpayReturn")
	public ResponseEntity<String> ecpayRequest(HttpServletRequest req) throws IOException {

		BufferedReader reader = req.getReader();
		StringBuilder reqBody = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			reqBody.append(line);
		}
		reader.close();

		System.out.println("ECPay傳過來的" + reqBody);

		ordersService.checkECPayReq(reqBody.toString());

		return ResponseEntity.ok("1|OK");
	}

	@GetMapping("/appointment/checkPayment")
	public ResponseEntity<Map<String, Object>> cehckPayment(HttpSession session) {
		Integer orderId = (Integer) session.getAttribute("orderId");
		Map result = ordersService.checkPayment(orderId);
		
		return ResponseEntity.ok(result);
	}
	


	

	@GetMapping("/appointment/calculateAmoute")
	public ResponseEntity<String> getAmoute(@RequestParam String origin, @RequestParam String destination) throws Exception{
		
		String result = ordersService.getAmoute(origin, destination);
		if("OutOfRange".equals(result)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OutOfRange");
		}
	
		return ResponseEntity.ok(result);
	}


	@GetMapping("/orderList")
	public String orderlist(HttpSession session, Model model) {
		List<OrdersVO> orderList = ordersService.getOrderByMemId((Integer) session.getAttribute("memId"));
		model.addAttribute("orderList", orderList);
		return "/front-end/orderList";
	}
	
	@GetMapping("/orders/detail/{orderId}")
	@ResponseBody
	public ResponseEntity<OrderDetailDTO> getOrderDetail(@PathVariable Integer orderId) {
	    OrdersVO order = ordersService.getOneOrder(orderId); 
	    OrderDetailDTO oderDetailDTO = ordersService.showOrderDetail(order);
	    return ResponseEntity.ok(oderDetailDTO);
	}
	
	@GetMapping("/order/cancelOrder")
	public String cancelOrder(@RequestParam("orderId") Integer orderId) {
		OrdersVO order = ordersService.getOneOrder(orderId);
		 if (order.getStatus() == 0 || order.getStatus() == 2) {
		        return "redirect:/orderList"; 
		    }
		LocalDateTime appointmentTime = ordersService.getOrderLocalDateTime(order);
		LocalDateTime now = LocalDateTime.now();
		   if (now.isAfter(appointmentTime.minusMinutes(30))) {
		        return "redirect:/orderList";
		    }
		    ordersService.updateStatus(orderId);
		    return "redirect:/orderList";
	}
	
	@PostMapping("/order/submitComment")
	public ResponseEntity<Object> submitComment(@RequestBody CommentDTO commentDTO) {
		try {
	        ordersService.saveComment(commentDTO);
	        return ResponseEntity.ok().build();
	    } catch (IllegalStateException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
}
