package com.orders.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
import com.orders.model.dto.OrderViewDTO;
import com.pet.model.PetRepository;
import com.pet.model.PetVO;
import com.schedule.model.ScheduleRepository;
import com.schedule.model.ScheduleVO;
import com.staff.model.StaffRepository;

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
	public Map<String, Object> addOrders(CheckoutOrderDTO checkoutOrderDTO, Integer memId ) {
		Map<String, Object> result = new HashMap<>();
		
		Integer payMethood = checkoutOrderDTO.getPayMethod();
		ScheduleVO scheuleVO = scheduleRepository.findById(checkoutOrderDTO.getSchId()).orElse(null);
		PetVO petVO = petRepository.findById(checkoutOrderDTO.getPetId()).orElse(null);
		System.out.println("-----");
		MemberVO memberVO = petVO.getMember();
		Integer apptTime = checkoutOrderDTO.getApptTime();
		
		//比對前端送過來的預約時段是否真的能預約
		if(!(scheuleVO.getDate().equals(checkoutOrderDTO.getDate()))  || !(scheuleVO.getTimeslot().substring(apptTime-1, apptTime+2).equals("111"))) {
			result.put("error", "班表");
			return result;
		}else {
			System.out.println("班表比對正常");
		}
		//比對前端送過來的寵物Id是否真的是目前登入的user的寵物
		if(!(memberVO.getMemId().equals(memId))) {
			result.put("error", "寵物");
			System.out.println("寵物會員比對沒過");
			return result;
		}else {
			System.out.println("寵物會員比對正常");
		}
		
		
		OrdersVO ordersVO = new OrdersVO();
		ordersVO.setMember(memberVO);
		ordersVO.setSchedule(scheuleVO);
		ordersVO.setStaff(scheuleVO.getStaffVO());
		ordersVO.setOnLocation(checkoutOrderDTO.getOnLocation());
		ordersVO.setOffLocation(checkoutOrderDTO.getOffLocation());
		ordersVO.setPoint(checkoutOrderDTO.getPoint());
		ordersVO.setPayment(checkoutOrderDTO.getPayment());
		ordersVO.setPayMethod(checkoutOrderDTO.getPayMethod());
		ordersVO.setNotes(checkoutOrderDTO.getNotes());
		ordersVO.setStatus(0);
		ordersVO = ordersRepository.save(ordersVO);
		
		result.put("orderId", ordersVO.getOrderId());
		
		OrderPetVO orderPetVO = new OrderPetVO();
		orderPetVO.setOrders(ordersVO);
		orderPetVO.setPet(petVO);
		orderpetRepository.save(orderPetVO);
		
		if(payMethood.equals(0)) {
			result.put("freeOrder", "true");
			return result;
		}
		LocalDateTime nowTime = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		Integer oderId = ordersVO.getOrderId();
		String orderTime = nowTime.format(dtf);
		String TradeNo = "P" + oderId+"T"+ orderTime.substring(2).replaceAll("[ /:]", "");
		String des = "Pet Taxi-" + TradeNo;
		System.out.println(TradeNo);
		
		AioCheckOutALL aco = new AioCheckOutALL();
		AllInOne all = new AllInOne("");
		aco.setMerchantID("3002607"); //店家ID
		aco.setMerchantTradeNo(TradeNo); //訂單ID
		aco.setMerchantTradeDate(orderTime); //訂單時間 yyyy/MM/dd HH:mm:ss
		switch (payMethood){
			case 0:
				System.out.println("全點數");
				break;
			case 1:
				System.out.println("全刷卡");
				aco.setTotalAmount(ordersVO.getPayment().toString()); //金額
				break;
			case 2:
				System.out.println("混和付款");
				aco.setTotalAmount( Integer.toString(ordersVO.getPayment()- ordersVO.getPoint()));
				break;
		}
		aco.setTradeDesc(des); //交易描述
		aco.setItemName("Pet Taxi"); //商品名稱
		aco.setNeedExtraPaidInfo("Y"); //額外資訊
		aco.setReturnURL("https://6f32-124-218-108-244.ngrok-free.app/ecpayReturn"); //付款結果通知 應為商家的controller
		// aco.setOrderResultURL(""); //付款完成後的結果參數 傳至前端用的
		aco.setClientBackURL("http://localhost:8080/appointment/paymentResults"); //付完錢後的返回商店按鈕會到的網址
		String form = all.aioCheckOut(aco, null);
		System.out.println(form);
		result.put("form", form);
		return result;
	}
	
	//檢查 ECPay回傳的付款結果資訊
	public void checkECPayReq(String reqBody){	
		String[] strArr = reqBody.toString().split("&");
		Hashtable<String, String> ECPayReq = new Hashtable<>();
		
		for(String str : strArr) {
			String[] keyValue = str.split("=", 2);
			String key = keyValue[0];
			String Value = keyValue.length > 1 ?  keyValue[1] : "";
			ECPayReq.put(key, Value);
		}
		Integer orderId = Integer.valueOf(ECPayReq.get("MerchantTradeNo").substring(1,3));
		System.out.println("訂單編號" + orderId);
		
		
		AllInOne aio = new AllInOne("");
		//檢查是否為ECPay回傳的資料
		if(aio.compareCheckMacValue(ECPayReq)) {
			System.out.println("比對通過");
			if("1".equals(ECPayReq.get("RtnCode"))) {
				System.out.println("已付款");
				//確定付款 更改訂單狀態、修改該員工班表
				ordersRepository.updateOrderStatus(1, orderId);
				Integer schId = ordersRepository.findById(orderId).orElse(null).getSchedule().getSchId();
				System.out.println(schId);
				scheduleRepository.updateBooked(schId);
				
			}else {
				System.out.println("未付款");
			}
		}else {
			System.out.println("比對沒過");
		}
	}
	
	public Map<String, Object> checkPayment(Integer orderId) {
		OrdersVO ordersVO = ordersRepository.findByOrderId(orderId);
		Map<String, Object> result = new HashMap<>();
		if(ordersVO == null) {
			return result;
		}
		
		if(ordersVO.getStatus().equals(1)) {
			result.put("pay", "1");
		}else {
			result.put("pay", "0");
		}
		String timeslot = ordersVO.getSchedule().getTimeslot();
		Integer apptTime = 0 ;
		 for (int i = 0; i < timeslot.length(); i++) {
	            if (timeslot.charAt(i) != '0') {
	            	apptTime = i+1;
	                break;  // 找到後就跳出迴圈
	            	}
	            }
		 PetVO petVO = ordersVO.getPet().get(0).getPet();
		
		OrderViewDTO orderViewDTO = new OrderViewDTO(
				ordersVO.getSchedule().getDate(),
				apptTime,
				ordersVO.getOnLocation(),
				ordersVO.getOffLocation(),
				ordersVO.getStaff().getStaffName(),
				ordersVO.getStaff().getStaffPhone(),
				ordersVO.getMember().getMemName(),
				ordersVO.getMember().getMemPhone(),
				petVO.getType(),
				petVO.getPetGender(),
				petVO.getPetName(),
				petVO.getWeight(),
				ordersVO.getNotes(),
				ordersVO.getPoint(),
				ordersVO.getPayment()
				);
		
		result.put("order", orderViewDTO);
		return result;
	}
}
