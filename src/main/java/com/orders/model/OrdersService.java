package com.orders.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.AioCheckOutALL;
import com.member.model.MemberRepository;
import com.member.model.MemberVO;
import com.orderpet.model.OrderPetRepository;
import com.orderpet.model.OrderPetVO;
import com.orders.model.dto.CheckoutOrderDTO;
import com.pet.model.PetRepository;
import com.pet.model.PetVO;
import com.schedule.model.ScheduleRepository;
import com.schedule.model.ScheduleVO;
import com.staff.model.StaffRepository;
import com.staff.model.StaffVO;

import jakarta.transaction.Transactional;

@Service("orderService")
public class OrdersService {

	@Autowired
	OrdersRepository ordersRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	StaffRepository staffRepository;

	@Autowired
	OrderPetRepository orderpetRepository;

	@Autowired
	PetRepository petRepository;

	public void addOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}

	public void updateOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}

	// 更新訂單狀態
	public void updateStatus(Integer memId) {
		ordersRepository.updateStatus(memId);
	}

	// 用訂單標號查詢訂單
	public OrdersVO getOneOrder(Integer orderId) {
		Optional<OrdersVO> optional = ordersRepository.findById(orderId);
		return optional.orElse(null);
	}

	// 查詢所有訂單
	public List<OrdersVO> getAll() {
		return ordersRepository.findAll();// 方法都不是自己寫的!(要先測試!)
	}

	@Transactional
	public String addOrders(CheckoutOrderDTO checkoutOrderDTO) {
		MemberVO memberVO = memberRepository.findById(checkoutOrderDTO.getMemId()).orElse(null);
		ScheduleVO scheuleVO = scheduleRepository.findById(checkoutOrderDTO.getSchId()).orElse(null);
		StaffVO staffVO = staffRepository.findById(checkoutOrderDTO.getStaffId()).orElse(null);
		PetVO petVO = petRepository.findById(checkoutOrderDTO.getPetId()).orElse(null);
		
		if (memberVO != null && scheuleVO != null && staffVO != null && petVO != null) {
			OrdersVO ordersVO = new OrdersVO();
			ordersVO.setMember(memberVO);
			ordersVO.setSchedule(scheuleVO);
			ordersVO.setStaff(staffVO);
			ordersVO.setOnLocation(checkoutOrderDTO.getOnLocation());
			ordersVO.setOffLocation(checkoutOrderDTO.getOffLocation());
			ordersVO.setPoint(checkoutOrderDTO.getPoint());
			ordersVO.setPayment(checkoutOrderDTO.getPayment());
			ordersVO.setPayMethod(checkoutOrderDTO.getPayMethod());
			ordersVO.setNotes(checkoutOrderDTO.getNotes());
			ordersVO.setStatus(0);
			ordersVO = ordersRepository.save(ordersVO);
			
			OrderPetVO orderPetVO = new OrderPetVO();
			orderPetVO.setOrders(ordersVO);
			orderPetVO.setPet(petVO);
			orderpetRepository.save(orderPetVO);
			
			LocalDateTime nowTime = LocalDateTime.now();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			Integer oderId = ordersVO.getOrderId();
			System.out.println("訂單時間"+ nowTime);
			String orderTime = nowTime.format(dtf);
			
			String TradeNo = "P" + oderId+"T"+ orderTime.substring(2).replaceAll("[ /:]", "");
			String des = "Pet Taxi-" + TradeNo;
			System.out.println(TradeNo);
			
			AioCheckOutALL aco = new AioCheckOutALL();
			AllInOne all = new AllInOne("");
			aco.setMerchantID("3002607"); //店家ID
			aco.setMerchantTradeNo(TradeNo); //訂單ID
			aco.setMerchantTradeDate(orderTime); //訂單時間 yyyy/MM/dd HH:mm:ss
			aco.setTotalAmount(ordersVO.getPayment().toString()); //金額
			aco.setTradeDesc(des); //交易描述
			aco.setItemName("Pet Taxi"); //商品名稱
			aco.setNeedExtraPaidInfo("Y"); //額外資訊
			aco.setReturnURL("https://c7ea-1-164-232-115.ngrok-free.app/"); //付款結果通知 應為商家的controller
			// aco.setOrderResultURL(""); //付款完成後的結果參數 傳至前端用的
			aco.setClientBackURL("http://localhost:8080/front-end/appointment"); //付完錢後的返回商店按鈕會到的網址
			String form = all.aioCheckOut(aco, null);
			System.out.println(form);
			return form;
		}
		return "error";
	}
}
