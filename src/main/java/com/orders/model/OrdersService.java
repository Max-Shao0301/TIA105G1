package com.orders.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orders.model.dto.CheckoutOrderDTO;

@Service("orderService")
public class OrdersService {

	@Autowired
	OrdersRepository ordersRepository;
	
	public void addOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}

	public void updateOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}
	
	//更新訂單狀態
	public void updateStatus(Integer memId) {
		ordersRepository.updateStatus(memId);
	}
	
	//用訂單標號查詢訂單
	public OrdersVO getOneOrder(Integer orderId) {
		Optional<OrdersVO> optional = ordersRepository.findById(orderId); 
		return optional.orElse(null);
	}
	
	//查詢所有訂單
	public List<OrdersVO> getAll() {
		return ordersRepository.findAll();//方法都不是自己寫的!(要先測試!)
	}
	
	
	
//	public String ordersToECPay(CheckoutOrderDTO checkoutOrderDTO) {
//		
//		return null;
//	}
}
