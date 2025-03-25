package com.orderpet.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderPetRepository  extends JpaRepository<OrderPetVO, Integer> {
	
	List<OrderPetVO> findByOrders_OrderId(Integer orderId);
}
