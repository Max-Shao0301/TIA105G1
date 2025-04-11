package com.orders.model;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface OrdersRepository extends JpaRepository<OrdersVO, Integer> {

	@Transactional
	@Modifying
	@Query(value = "UPDATE orders SET status = 0 WHERE order_id = ?1 ", nativeQuery = true)
	void updateStatus(Integer orderId);

	@Transactional
	@Modifying
	@Query(value = "UPDATE orders SET status = ?1 WHERE order_id = ?2 ", nativeQuery = true)
	void updateOrderStatus(Integer status, Integer orderId);

	@EntityGraph(attributePaths = { "member", "schedule", "staff", "pet" })
	OrdersVO findByOrderId(Integer orderId);
	
	@EntityGraph(attributePaths = { "member", "schedule", "staff", "pet" })
	@Query("SELECT o FROM OrdersVO o WHERE o.member.memId = :memId")
	List<OrdersVO> findByMemId(@Param("memId") Integer memId);
	
	
	@Query(value = "SELECT COUNT(*) FROM orders WHERE mem_id = ?1", nativeQuery = true)
	Integer getOrderAmount(Integer memId);
	
	@Query(value = "SELECT * FROM orders WHERE mem_id = :memId ORDER BY order_id DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<OrdersVO> findOrdersByMemIdWithPagination(@Param("memId") Integer memId,
	                                               @Param("offset") int offset,
	                                               @Param("limit") int limit);
	
	@EntityGraph(attributePaths = { "schedule", "staff", "pet", "pet.pet" })
	@Query("SELECT DISTINCT o FROM OrdersVO o " +
	       "JOIN o.schedule s " +
	       "JOIN o.staff staff " +
	       "JOIN o.pet op " +
	       "JOIN op.pet pet " +
	       "WHERE o.member.memId = :memId AND (" +
	       "o.onLocation LIKE %:keyword% " +
	       "OR o.offLocation LIKE %:keyword% " +
	       "OR staff.staffName LIKE %:keyword% " +
	       "OR staff.carNumber LIKE %:keyword% " +
	       "OR pet.petName LIKE %:keyword%)")
	List<OrdersVO> findByMemberAndKeyword(@Param("memId") Integer memId, @Param("keyword") String keyword);
	
	@Query(value = "SELECT" +
			" o.order_id, o.on_location, o.off_location, o.payment, o.notes, o.status, o.star, o.rating, o.picture," +
			" p.type, p.pet_name, p.pet_gender, p.weight," +
			" s.timeslot, s.date," +
			" m.mem_phone " +
			"FROM orders o " +
            "JOIN order_pet op ON o.order_id = op.order_id " +
            "JOIN pet p ON op.pet_id = p.pet_id " +
            "JOIN schedule s ON o.sch_id = s.sch_id " +
            "JOIN member m ON o.mem_id = m.mem_id " +
            "WHERE o.staff_id = ?1 AND (o.status = 1 OR o.status = 2)", nativeQuery = true)
	List<Object[]> findByStaff(Integer staffId);
}
