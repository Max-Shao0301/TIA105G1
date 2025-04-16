package com.orders.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.AioCheckOutOneTime;
import com.member.model.MemberRepository;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.orderpet.model.OrderPetRepository;
import com.orderpet.model.OrderPetVO;
import com.orders.model.dto.CheckoutOrderDTO;
import com.orders.model.dto.CommentDTO;
import com.orders.model.dto.OrderDetailDTO;
import com.orders.model.dto.OrderViewDTO;
import com.pet.model.PetRepository;
import com.pet.model.PetVO;
import com.schedule.model.ScheduleRepository;
import com.schedule.model.ScheduleVO;
import com.springbootmail.MailService;
import com.staff.model.StaffRepository;

import jakarta.servlet.http.HttpSession;
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

	@Autowired
	MemberService memberService;
	
	@Autowired
	MailService mailService;

	@Value("${google.maps.api.key}")
	private String googleMapApiKey;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private StringRedisTemplate redisTemplate;

	public void addOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}

	public void updateOrders(OrdersVO ordersVO) {
		ordersRepository.save(ordersVO);
	}

	// 更新訂單狀態
	public void updateStatus(Integer orderId) {
		ordersRepository.updateStatus(orderId);
		Integer schId = getOneOrder(orderId).getSchedule().getSchId();
		scheduleRepository.updateUnbooked(schId);
	}

	// 用訂單標號查詢訂單
	public OrdersVO getOneOrder(Integer orderId) {
		Optional<OrdersVO> optional = ordersRepository.findById(orderId);
		return optional.orElse(null);
	}

	// 用會員標號查詢訂單
	public List<OrdersVO> getOrderByMemId(Integer memId) {
		List<OrdersVO> optional = ordersRepository.findByMemId(memId);
		return optional;
	}

	// 查詢所有訂單
	public List<OrdersVO> getAll() {
		return ordersRepository.findAll();// 方法都不是自己寫的!(要先測試!)
	}

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);// 設定任務的執行池數量
	private final ConcurrentHashMap<Integer, ScheduledFuture<?>> tradeNoMap = new ConcurrentHashMap<>(); // 儲存任務用

	// 變更資料庫的方法而外拆開來加Transactional，確保執行安全
	@Transactional
	public void noPaymentOrder(Integer orderId) {
		Integer schId = ordersRepository.findById(orderId).orElse(null).getSchedule().getSchId();
		scheduleRepository.updateUnbooked(schId);
		ordersRepository.updateOrderStatus(0, orderId);
		tradeNoMap.remove(orderId);// 執行完後把orderId對應的任務從map中移除
	}

	// 計時十分鐘，如果超過十分鐘未付款則視為訂單取消
	public void paymentCountdown(Integer orderId) {
		Runnable nonPayment = () -> { // 超過十分鐘時所要執行的任務
			System.out.println(orderId + "未付款");
			noPaymentOrder(orderId);
		};
		// 使用ScheduledFuture設定十分鐘後執行任務
		ScheduledFuture<?> take = scheduler.schedule(nonPayment, 10, TimeUnit.MINUTES);
		tradeNoMap.put(orderId, take); // 用orderId當作key將任務儲存至map中
		System.out.println("計時開始 orderId = " + orderId);
	}

	public void stopPaymentCountdown(Integer orderId) {
		ScheduledFuture<?> take = tradeNoMap.get(orderId);
		if (take != null) {
			take.cancel(true); // 取消計時器的排程任務
			tradeNoMap.remove(orderId); // 把orderId對應的任務從map中移除
			System.out.println("確認付款 取消任務，orderId = " + orderId);
		}
	}

	@Transactional
	public Map<String, Object> addOrders(CheckoutOrderDTO checkoutOrderDTO, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		Integer memId = (Integer) session.getAttribute("memId");
		Integer checkoutAamount = (Integer) session.getAttribute("amoute"); // 取得事先存好的訂單金額
		// 拿出結帳訂單中的班表跟寵物物件
		ScheduleVO scheuleVO = scheduleRepository.findById(checkoutOrderDTO.getSchId()).orElse(null);
		PetVO petVO = petRepository.findById(checkoutOrderDTO.getPetId()).orElse(null);

		MemberVO memberVO = petVO.getMember();
		if(!memberService.infoIsComplete(memberVO)) {
			result.put("error", "memberInfoError");
			System.out.println("資料不齊全");
			return result;
		}
		Integer apptTime = checkoutOrderDTO.getApptTime();

		// 比對前端送過來的預約時段是否真的能預約
		if (!(scheuleVO.getDate().equals(checkoutOrderDTO.getDate()))
				|| !(scheuleVO.getTimeslot().substring(apptTime, apptTime + 3).equals("111"))) {
			result.put("error", "schError");
			System.out.println("比對不成功");
			return result;
		} else {
			System.out.println("班表比對正常");
		}
		// 比對前端送過來的寵物Id是否真的是目前登入的user的寵物
		if (!(memberVO.getMemId().equals(memId))) {
			result.put("error", "petError");
			System.out.println("寵物會員比對沒過");
			return result;
		} else {
			System.out.println("寵物會員比對正常");
		}

		// 從結帳訂單中拿出訂單使用的點數、支付方法，來跟此會員物件的點數比對 確保資料正確
		Integer payMethood = checkoutOrderDTO.getPayMethod();
		Integer memberPoint = memberVO.getPoint();
		Integer orderPoint = checkoutOrderDTO.getPoint();
		System.out.println("會員點數" + memberPoint);
		System.out.println("訂單點數" + orderPoint);
		// 用點數支付
		if (payMethood.equals(0)) {
			// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、且點數足夠支付整筆訂單金額
			if (memberPoint.equals(orderPoint) && memberPoint > checkoutAamount) {
				result.put("freeOrder", "true");
				memberRepository.updatePoint(memberPoint - checkoutAamount, memId);
				orderPoint = checkoutAamount;
				payMethood = 0;

				// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、但點數不夠支付整筆訂單金額、而且user點數不是0
			} else if (memberPoint.equals(orderPoint) && memberPoint < checkoutAamount && !(memberPoint.equals(0))) {
				checkoutAamount -= memberPoint;
				payMethood = 2;
				System.out.println("點數+現金");
			} else if (!(memberPoint.equals(orderPoint))) {
				result.put("error", "pointError");
			}
		}
		// 建立訂單物件
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
		ordersVO.setStatus(payMethood.equals(0) ? 1 : 3);
		ordersVO = ordersRepository.save(ordersVO); // 儲存訂單物件

		result.put("orderId", ordersVO.getOrderId());

		OrderPetVO orderPetVO = new OrderPetVO();
		orderPetVO.setOrders(ordersVO);
		orderPetVO.setPet(petVO);
		orderpetRepository.save(orderPetVO); // 儲存寵物訂單物件
		scheduleRepository.updateBooked(scheuleVO.getSchId()); // 訂單建立後直接將班表改成已預約，防止二次預約
		if (payMethood.equals(0)) { // 確定支付方式為全點數付款則直接返回，不必串接綠界金流
			session.setAttribute("orderId", ordersVO.getOrderId());
			return result;
		}
		// 準備處理綠界金流串接 建立金流用的訂單物件
		LocalDateTime nowTime = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		Integer orderId = ordersVO.getOrderId();
		String orderTime = nowTime.format(dtf);
		// 訂單編號用資料庫的P+訂單編號+T+時間
		String TradeNo = "P" + orderId + "T" + orderTime.substring(2).replaceAll("[ /:]", "");
		String des = "Pet Taxi-" + TradeNo;
		System.out.println(TradeNo);
		System.out.println(orderTime);
		AioCheckOutOneTime aco = new AioCheckOutOneTime(); // 僅信用卡一次付清的訂單物件
		AllInOne all = new AllInOne("");
		aco.setMerchantID("3002607"); // 店家ID
		aco.setMerchantTradeNo(TradeNo); // 訂單ID
		aco.setMerchantTradeDate(orderTime); // 訂單時間 yyyy/MM/dd HH:mm:ss
		aco.setTotalAmount(checkoutAamount.toString()); // 金額
		aco.setTradeDesc(des); // 交易描述
		aco.setItemName("Pet Taxi"); // 商品名稱
		aco.setNeedExtraPaidInfo("Y"); // 額外資訊
		aco.setReturnURL("https://c1c5-1-164-241-28.ngrok-free.app/ecpayReturn"); // 付款結果通知 應為商家的controller
		// aco.setOrderResultURL(""); //付款完成後的結果參數 傳至前端用的
		aco.setClientBackURL("http://localhost:8080/appointment/paymentResults"); // 付完錢後的返回商店按鈕會到的網址

		//使用ECPay建立付款頁面的方法 兩個參數分別是訂單物件跟發票物件 不開發票第二個就傳null
		String form = all.aioCheckOut(aco, null); 

		result.put("form", form);
		session.setAttribute("orderId", orderId);
		paymentCountdown(orderId); // 呼叫結帳倒數計時
		return result;
	}

	// 檢查 ECPay回傳的付款結果資訊
	@Transactional
	public void checkECPayReq(String reqBody) {
		String[] strArr = reqBody.split("&");
		Hashtable<String, String> ECPayReq = new Hashtable<>();

		for (String str : strArr) {
			String[] keyValue = str.split("=", 2);
			String key = keyValue[0];
			String Value = keyValue.length > 1 ? keyValue[1] : "";
			ECPayReq.put(key, Value);
		}
		Integer orderId = Integer.valueOf(ECPayReq.get("MerchantTradeNo").substring(1, 3));
		System.out.println("訂單編號" + orderId);
		AllInOne aio = new AllInOne("");
		// 檢查是否為ECPay回傳的資料
		if (aio.compareCheckMacValue(ECPayReq)) {
			System.out.println("比對通過");
			if ("1".equals(ECPayReq.get("RtnCode"))) {
				System.out.println("已付款");
				ordersRepository.updateOrderStatus(1, orderId);
				stopPaymentCountdown(orderId);
				OrdersVO ordersVO = ordersRepository.findById(orderId).orElseGet(null);
				// 調訂單資料出來 組回傳用的信件
				String email = ordersVO.getMember().getMemEmail();
				String timeslot = ordersVO.getSchedule().getTimeslot();
				Integer apptTime = 0;
				// 用迴圈索引的方式找第一個不是0的數，藉此來辨別這個班表的起始預約時間是幾點
				for (int i = 0; i < timeslot.length(); i++) {
					if (timeslot.charAt(i) != '0') {
						apptTime = i;
						break; // 找到後就跳出迴圈
					}
				}
				PetVO petVO = ordersVO.getPet().get(0).getPet();

				String subject = "寵愛牠-預約成功通知";
				String content = "預約項目：寵物接送\n" + "預約時間：" + ordersVO.getSchedule().getDate() + " " + apptTime + ":00\n"
						+ "上車地址：" + ordersVO.getOnLocation() + "\n" + "目的地地址：" + ordersVO.getOffLocation() + "\n"
						+ "預約服務人員：" + ordersVO.getStaff().getStaffName() + "\n" + "服務人員聯絡電話："
						+ ordersVO.getStaff().getStaffPhone() + "\n" + "會員姓名：" + ordersVO.getMember().getMemName()
						+ "\n" + "會員電話：" + ordersVO.getMember().getMemPhone() + "\n" + "毛小孩類別："
						+ (petVO.getType().equals("cat") ? "貓" : "狗") + "\n" + "毛小孩性別："
						+ (petVO.getPetGender().equals(1) ? "公" : "母") + "\n" + "毛小孩大名：" + petVO.getPetName() + "\n"
						+ "毛小孩體重：" + petVO.getWeight() + "kg\n" + "其他注意事項：" + ordersVO.getNotes() + "\n\n" + "請留意預約時間";
				System.out.println(content);
				mailService.sendPlainText(Collections.singleton(email), subject, content);
			} else {
				// 收到的不是付款成功通知時 更改訂單狀態、修改該員工班表
				Integer schId = ordersRepository.findById(orderId).orElse(null).getSchedule().getSchId();
				scheduleRepository.updateUnbooked(schId);
				ordersRepository.updateOrderStatus(0, orderId);
				System.out.println("未付款");
			}
		} else {
			System.out.println("比對沒過");
		}
	}

	// 前端的驗證付款結果用的方法
	public Map<String, Object> checkPayment(HttpSession session) {
		Integer orderId = (Integer) session.getAttribute("orderId");
		OrdersVO ordersVO = ordersRepository.findByOrderId(orderId);

		Map<String, Object> result = new HashMap<>();
		if (ordersVO == null) {
			return result;
		}
		// 上面調出訂單物件後，比對訂單物件的狀態 確認是否已付款，以及如果付款了並且使用點數+現金 則把使用者的點數歸0
		// 如果用點數+現金代表他點數一定不夠支付整筆訂單費用，所以不可能剩點數
		if (ordersVO.getStatus().equals(1)) {
			result.put("pay", "1");
			System.out.println("已成立" + ordersVO.getStatus());
			if (ordersVO.getPayMethod().equals(2)) {
				memberRepository.updatePoint(0, ordersVO.getMember().getMemId());
			}
		} else {
			result.put("pay", "0");
			System.out.println("未成立" + ordersVO.getStatus());
		}

		String timeslot = ordersVO.getSchedule().getTimeslot();
		Integer apptTime = 0;
		// 用迴圈索引的方式找第一個不是0的數，藉此來辨別這個班表的起始預約時間是幾點
		for (int i = 0; i < timeslot.length(); i++) {
			if (timeslot.charAt(i) != '0') {
				apptTime = i;
				break; // 找到後就跳出迴圈
			}
		}
		PetVO petVO = ordersVO.getPet().get(0).getPet();
		// 建立訂單檢視的物件
		OrderViewDTO orderViewDTO = new OrderViewDTO(ordersVO.getSchedule().getDate(), apptTime,
				ordersVO.getOnLocation(), ordersVO.getOffLocation(), ordersVO.getStaff().getStaffName(),
				ordersVO.getStaff().getStaffPhone(), ordersVO.getMember().getMemName(),
				ordersVO.getMember().getMemPhone(), petVO.getType(), petVO.getPetGender(), petVO.getPetName(),
				petVO.getWeight(), ordersVO.getNotes(), ordersVO.getPoint(), ordersVO.getPayment());

		result.put("order", orderViewDTO);
		return result;
	}

	public String getAmoute(String origin, String destination, HttpSession session) {
		final String ROUTES_API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
		final Integer STARTPRICE = 100; // 起跳價
		final Integer PRICEPERKM = 50; // 每公里價格
		Integer amoute = null;
		// 去除前面的數字 避免郵遞區號影響範圍判斷
		origin = origin.replaceFirst("^\\d+", "").trim();
		destination = destination.replaceFirst("^\\d+", "").trim();
		// 判斷是否為在新北跟台北的範圍內
		if (!(origin.startsWith("新北市") || origin.startsWith("台北市"))
				|| !(destination.startsWith("新北市") || destination.startsWith("台北市"))) {
			return "OutOfRange";
		}
		// 把地址資料塞req，並且將模式設定成開車
		Map<String, Object> reqToRoutes = new HashMap<>();
		reqToRoutes.put("origin", Map.of("address", origin));
		reqToRoutes.put("destination", Map.of("address", destination));
		reqToRoutes.put("travelMode", "DRIVE");

		// 設定header
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content_Type", "application/json"); // 我們發送的請求格式
		headers.set("X-Goog-Api-Key", googleMapApiKey); // API KEY google要的
		headers.set("X-Goog-FieldMask", "routes.distanceMeters"); // 設定好回傳只要拿哪些資料 減少API費用

		// 請求實體中放入body跟header
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(reqToRoutes, headers);

		// 發送請求用的類別 spring提供的
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Map> res = restTemplate.postForEntity(ROUTES_API_URL, entity, Map.class);

		// 拆解得到的回應
		Map<String, Object> body = res.getBody();
		List<Map<String, Object>> routes = (List<Map<String, Object>>) body.get("routes");
		// 取得資料後轉成double再加上公式 四捨五入成金額
		if (!routes.isEmpty()) {
			Integer distanceInt = (Integer) routes.get(0).get("distanceMeters");
			Double distance = Math.round((distanceInt / 1000.0) * 10) / 10.0;
			amoute = (distance > 1 ? (int) Math.round(distance * PRICEPERKM + STARTPRICE) : STARTPRICE);
			System.out.println("距離" + distance);
		}
		System.out.println("金額" + amoute);
		session.setAttribute("amoute", amoute);
		return amoute.toString();
	}

	public OrderDetailDTO showOrderDetail(OrdersVO order) {

		OrderDetailDTO oderDetailDTO = new OrderDetailDTO();
		oderDetailDTO.setOrderId(order.getOrderId());
		oderDetailDTO.setOrderStatus(order.getStatus());
		oderDetailDTO.setAppointmentTime(format(getOrderLocalDateTime(order)));
		oderDetailDTO.setCreatedTime(format(order.getCreateTime()));
		oderDetailDTO.setUpdateTime(format(order.getUpdateTime()));
		oderDetailDTO.setOnLocation(order.getOnLocation());
		oderDetailDTO.setOffLocation(order.getOffLocation());
		oderDetailDTO.setCarNumber(order.getStaff().getCarNumber());
		oderDetailDTO.setStaffName(order.getStaff().getStaffName());
		oderDetailDTO.setPet(order.getPet().get(0).getPet().getPetName());
		oderDetailDTO.setPayment(order.getPayment());
		oderDetailDTO.setPayMethod(order.getPayMethod());
		oderDetailDTO.setPoint(order.getPoint());
		oderDetailDTO.setNotes(order.getNotes());
		oderDetailDTO.setRating(order.getRating());
		oderDetailDTO.setStar(order.getStar());

		return oderDetailDTO;
	}

	public String format(LocalDateTime localDateTime) {
		return localDateTime != null ? localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "";
	}

	public LocalDateTime getOrderLocalDateTime(OrdersVO order) {
		Integer startTime;
		String slot = order.getSchedule().getTimeslot();
		LocalDate date = order.getSchedule().getDate();

		if (slot.contains("1")) {
			startTime = slot.indexOf("1");
		} else {
			startTime = slot.indexOf("2");
		}
		String timeStr = date + " " + LocalTime.of(startTime, 0); // LocalTime.of(小時, 分鐘) 用 LocalTime.of(9, 0) → 得到
																	// 09:00
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime localDateTime = LocalDateTime.parse(timeStr, formatter);
		return localDateTime;
	}

	public void saveComment(CommentDTO commentDTO) {
		OrdersVO ordersVO = getOneOrder(commentDTO.getOrderId());
		if (ordersVO.getStatus() != 2) {
			throw new IllegalStateException("只有已完成的訂單可以評價");
		}

		if (ordersVO.getStar() != null || ordersVO.getRating() != null) {
			throw new IllegalStateException("此訂單已評價過");
		}
		ordersVO.setStar(commentDTO.getStar());
		ordersVO.setRating(commentDTO.getRating());
		updateOrders(ordersVO);
	}

	public Integer getPageTotal(Integer memId, Integer pageSize) {
		Integer total = ordersRepository.getOrderAmount(memId);
		System.out.println(total + "ahah");
		Integer pageQty = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
		return pageQty;
	}

	public List<OrdersVO> getOrdersByPage(Integer memId, Integer page, Integer pageSize) {
		int offset = (page - 1) * pageSize; // 從第幾筆開始查
		List<OrdersVO> orderVO = ordersRepository.findOrdersByMemIdWithPagination(memId, offset, pageSize);
		for (OrdersVO order : orderVO) {
			LocalDateTime time = getOrderLocalDateTime(order);
			String ordertime = format(time);
			order.setNotes(ordertime);
		}
		return orderVO;
	}

	public List<OrdersVO> searchOrdersByKeyword(Integer memId, String keyword) {
		List<OrdersVO> list = ordersRepository.findByMemberAndKeyword(memId, keyword);
		for (OrdersVO order : list) {
			LocalDateTime time = getOrderLocalDateTime(order);
			String ordertime = format(time);
			order.setNotes(ordertime);
		}
		return list;
	}

	// 只查該服務人員的訂單
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

	// 取消訂單新增點數
	public void addPoints(Integer orderId) {
		OrdersVO orderVO = ordersRepository.findByOrderId(orderId);
		MemberVO memberVO = orderVO.getMember();
		Integer memPoint = memberVO.getPoint();
		Integer addPoint = orderVO.getPayment();
		Integer totalPoint = memPoint + addPoint;
		System.out.println(memPoint + "+" + addPoint + "=" + totalPoint);
		memberVO.setPoint(totalPoint);
		memberRepository.save(memberVO);

	}

	@Scheduled(fixedRate = 600 * 1000) // 600s
	public void checkAndSendReminders() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime sixtyMinLater = now.plusMinutes(240);

		
		List<OrdersVO> orders = ordersRepository.findOrdersTodayAndTomorrow();
		for (OrdersVO order : orders) {
			LocalDateTime appointmentTime = getOrderLocalDateTime(order);
			Integer memId =  order.getMember().getMemId();
			if (appointmentTime.isAfter(now) && appointmentTime.isBefore(sixtyMinLater)) {
				// 推播提醒給該會員
				String key = "reminder:" + memId;
				String message = "🔔 您的預約訂單 " + memId + " 將於 " + format(appointmentTime) + " 開始，\n  請做好出發的準備！";
				
				redisTemplate.opsForList().rightPush(key, message);
				System.out.println("存在redis" + memId);
				// 嘗試推播給在線會員
				try {
					System.out.println("推播給" + memId);
				    messagingTemplate.convertAndSend("/topic/reminder/" + memId, message);
				} catch (Exception e) {
					
					
				}

				// 標記為已提醒（避免重複提醒）
				order.setReminded(true);
				ordersRepository.save(order);
			}
		}
	}
}
