package com.orders.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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
	
	//只查該服務人員的訂單
	public List<OrdersVO> getstaffOrder(Integer staffId) {
	    List<Object[]> results = ordersRepository.findByStaff(staffId);
	    Map<Integer, OrdersVO> ordersMap = new HashMap<>();

	    for (Object[] result : results) {

	        int orderIdIndex = 0;
	        int onLocationIndex = 1;
	        int offLocationIndex = 2;
	        int paymentIndex = 3;
	        int notesIndex = 4;
	        int statusIndex = 5;
	        int starIndex = 6;
	        int ratingIndex = 7;
	        int pictureIndex = 8;
	        int petTypeIndex = 9;
	        int petNameIndex = 10;
	        int petGenderIndex = 11;
	        int petWeightIndex = 12;
	        int scheduleTimeslotIndex = 13;
	        int scheduleDateIndex = 14;
	        int memberPhoneIndex = 15;

	        Integer orderId = (Integer) result[orderIdIndex];
	        OrdersVO order = ordersMap.get(orderId);
	        if (order == null) {
	            order = new OrdersVO();
	            order.setOrderId(orderId);
	            order.setOnLocation((String) result[onLocationIndex]);
	            order.setOffLocation((String) result[offLocationIndex]);
	            order.setPayment((Integer) result[paymentIndex]);
	            order.setNotes((String) result[notesIndex]);
	            order.setStatus((Integer) result[statusIndex]);
	            order.setStar((Integer) result[starIndex]);
	            order.setRating((String) result[ratingIndex]);
	            order.setPicture((byte[]) result[pictureIndex]);
	            order.setPet(new ArrayList<>());
	            order.setSchedule(new ScheduleVO());
	            order.setMember(new MemberVO());

	            // ScheduleVO的部分
	            if (result[scheduleTimeslotIndex] != null) {
	                order.getSchedule().setTimeslot((String) result[scheduleTimeslotIndex]);
	            }
	            if (result[scheduleDateIndex] != null) {
	                java.sql.Timestamp timestamp = (java.sql.Timestamp) result[scheduleDateIndex];
	                order.getSchedule().setDate(timestamp.toLocalDateTime().toLocalDate());
	            }

	            // MemberVO的部分
	            if (result[memberPhoneIndex] != null) {
	                order.getMember().setMemPhone((String) result[memberPhoneIndex]);
	            }

	            ordersMap.put(orderId, order);
	        }

	        PetVO petVO = new PetVO();
	        petVO.setType((String) result[petTypeIndex]);
	        petVO.setPetName((String) result[petNameIndex]);
	        petVO.setPetGender((Integer) result[petGenderIndex]);
	        petVO.setWeight((Integer) result[petWeightIndex]);

	        OrderPetVO orderPet = new OrderPetVO();
	        orderPet.setOrders(order);
	        orderPet.setPet(petVO);

	        order.getPet().add(orderPet);
	        
	    }

	    return new ArrayList<>(ordersMap.values());
	    
	}


	// 查詢所有訂單
	public List<OrdersVO> getAll() {
		return ordersRepository.findAll();// 方法都不是自己寫的!(要先測試!)
	}

	@Transactional
	public Map<String, Object> addOrders(CheckoutOrderDTO checkoutOrderDTO, Integer memId ) {
		Map<String, Object> result = new HashMap<>();
		
		
		ScheduleVO scheuleVO = scheduleRepository.findById(checkoutOrderDTO.getSchId()).orElse(null);
		PetVO petVO = petRepository.findById(checkoutOrderDTO.getPetId()).orElse(null);

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
		
		Integer payMethood = checkoutOrderDTO.getPayMethod();
		Integer memberPoint = memberVO.getPoint();
		Integer orderPoint = checkoutOrderDTO.getPoint();
		System.out.println("會員點數"+ memberPoint);
		System.out.println("訂單點數"+ orderPoint);
		//這是最終會丟給ECPay的結帳金額 而非存入資料庫的結帳金額
		Integer checkoutAamount = checkoutOrderDTO.getPayment();
		//用點數支付
		if(payMethood.equals(0)) {
			// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、且點數足夠支付整筆訂單金額
			if(memberPoint.equals(orderPoint) && memberPoint > checkoutAamount){
				result.put("freeOrder", "true");
				memberRepository.updatePoint(memberPoint - checkoutAamount, memId);
				scheduleRepository.updateBooked(scheuleVO.getSchId());
				orderPoint = checkoutAamount;
				payMethood = 0;
				System.out.println("點數單");
			// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、但點數不夠支付整筆訂單金額、而且user點數不是0
			}else if (memberPoint.equals(orderPoint) && memberPoint < checkoutAamount && !(memberPoint.equals(0))) {
				checkoutAamount -= memberPoint;
				payMethood = 2;
				System.out.println("點數+現金");
			}else if(!(memberPoint.equals(orderPoint))) {
				result.put("error", "點數");
				System.out.println("錯錯錯");
			}
		}
		
		OrdersVO ordersVO = new OrdersVO();
		ordersVO.setMember(memberVO);
		ordersVO.setSchedule(scheuleVO);
		ordersVO.setStaff(scheuleVO.getStaffVO());
		ordersVO.setOnLocation(checkoutOrderDTO.getOnLocation());
		ordersVO.setOffLocation(checkoutOrderDTO.getOffLocation());
		ordersVO.setPoint(orderPoint);
		ordersVO.setPayment(checkoutOrderDTO.getPayment());
		ordersVO.setPayMethod(payMethood);
		ordersVO.setNotes(checkoutOrderDTO.getNotes());
		ordersVO.setStatus(payMethood.equals(0)? 1 : 0);
		ordersVO = ordersRepository.save(ordersVO);
		
		result.put("orderId", ordersVO.getOrderId());
		
		OrderPetVO orderPetVO = new OrderPetVO();
		orderPetVO.setOrders(ordersVO);
		orderPetVO.setPet(petVO);
		orderpetRepository.save(orderPetVO);
		
		if(payMethood.equals(0)) {
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
		aco.setTotalAmount(checkoutAamount.toString()); //金額
		aco.setTradeDesc(des); //交易描述
		aco.setItemName("Pet Taxi"); //商品名稱
		aco.setNeedExtraPaidInfo("Y"); //額外資訊
		aco.setReturnURL("https://feca-1-164-226-222.ngrok-free.app/ecpayReturn"); //付款結果通知 應為商家的controller
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
			memberRepository.updatePoint(0, ordersVO.getMember().getMemId());
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
