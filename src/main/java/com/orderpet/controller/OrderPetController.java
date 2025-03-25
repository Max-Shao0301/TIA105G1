package com.orderpet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.orderpet.model.OrderPetService;

@Controller
public class OrderPetController {
	
	@Autowired
	private OrderPetService orderPetService;
	
	@GetMapping("/appointment")
	public String getAppointnetn() {
		
		return "/front-end/appointment";
	}
}
