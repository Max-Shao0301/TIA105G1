package com.orders.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;


public interface OrdersRepository extends JpaRepository<OrdersVO, Integer> {
	
	@Transactional
	@Modifying
	@Query(value="UPDATE orders SET status = 0 WHERE order_id = ?1 ", nativeQuery = true)
	void updateStatus(Integer orderId);
	
	
}
