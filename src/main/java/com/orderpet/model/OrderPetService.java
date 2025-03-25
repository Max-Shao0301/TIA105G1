package com.orderpet.model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderPetService")
public class OrderPetService {

	@Autowired
	private OrderPetRepository orderPetRepository;

	public void addPet(OrderPetVO orderPetVO) {
		orderPetRepository.save(orderPetVO);
	}

	public OrderPetVO findById(Integer ordPetId) {
		Optional<OrderPetVO> orderPet = orderPetRepository.findById(ordPetId);
		return orderPet.orElse(null);
	}

	//用訂單ID找資料，staff跟member的訂單檢視頁面用
	public OrderPetVO findByOrderId(Integer orderId) {
		Optional<OrderPetVO> orderPet = orderPetRepository.findById(orderId);
		return orderPet.orElse(null);
	}

}
