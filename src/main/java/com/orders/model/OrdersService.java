package com.orders.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecpay.payment.integration.AllInOne;
import com.ecpay.payment.integration.domain.AioCheckOutOneTime;
import com.ecpay.payment.integration.domain.QueryTradeInfoObj;
import com.member.model.MemberRepository;
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

	@Value("{google.maps.api.key}")
	private  String  googleMapApiKey;

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

	// 用會員標號查詢訂單
	public List<OrdersVO> getOrderByMemId(Integer memId) {
		List<OrdersVO> optional = ordersRepository.findByMemId(memId);
		return optional;
	}

	// 查詢所有訂單
	public List<OrdersVO> getAll() {
		return ordersRepository.findAll();// 方法都不是自己寫的!(要先測試!)
	}

	@Transactional
	public Map<String, Object> addOrders(CheckoutOrderDTO checkoutOrderDTO, Integer memId, Integer checkoutAamount) {
		Map<String, Object> result = new HashMap<>();

		ScheduleVO scheuleVO = scheduleRepository.findById(checkoutOrderDTO.getSchId()).orElse(null);
		PetVO petVO = petRepository.findById(checkoutOrderDTO.getPetId()).orElse(null);

		MemberVO memberVO = petVO.getMember();
		Integer apptTime = checkoutOrderDTO.getApptTime();

		// 比對前端送過來的預約時段是否真的能預約
		if (!(scheuleVO.getDate().equals(checkoutOrderDTO.getDate()))
				|| !(scheuleVO.getTimeslot().substring(apptTime - 1, apptTime + 2).equals("111"))) {
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

		Integer payMethood = checkoutOrderDTO.getPayMethod();
		Integer memberPoint = memberVO.getPoint();
		Integer orderPoint = checkoutOrderDTO.getPoint();
		System.out.println("會員點數" + memberPoint);
		System.out.println("訂單點數" + orderPoint);
		// 這是最終會丟給ECPay的結帳金額 而非存入資料庫的結帳金額
//		Integer checkoutAamount = checkoutOrderDTO.getPayment();
		// 用點數支付
		if (payMethood.equals(0)) {
			// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、且點數足夠支付整筆訂單金額
			if (memberPoint.equals(orderPoint) && memberPoint > checkoutAamount) {
				result.put("freeOrder", "true");
				memberRepository.updatePoint(memberPoint - checkoutAamount, memId);
//				scheduleRepository.updateBooked(scheuleVO.getSchId());
				orderPoint = checkoutAamount;
				payMethood = 0;
				System.out.println("點數單");
				// 驗證前端提交過來的點數是否與資料庫儲存的會員點數一致、但點數不夠支付整筆訂單金額、而且user點數不是0
			} else if (memberPoint.equals(orderPoint) && memberPoint < checkoutAamount && !(memberPoint.equals(0))) {
				checkoutAamount -= memberPoint;
				payMethood = 2;
				System.out.println("點數+現金");
			} else if (!(memberPoint.equals(orderPoint))) {
				result.put("error", "pointError");
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
		ordersVO.setStatus(1);
		ordersVO = ordersRepository.save(ordersVO);

		result.put("orderId", ordersVO.getOrderId());

		OrderPetVO orderPetVO = new OrderPetVO();
		orderPetVO.setOrders(ordersVO);
		orderPetVO.setPet(petVO);
		orderpetRepository.save(orderPetVO);
		scheduleRepository.updateBooked(scheuleVO.getSchId());
		if (payMethood.equals(0)) {
			return result;
		}

		LocalDateTime nowTime = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		Integer orderId = ordersVO.getOrderId();
		String orderTime = nowTime.format(dtf);
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
		aco.setReturnURL("https://8377-124-218-108-244.ngrok-free.app/ecpayReturn"); // 付款結果通知 應為商家的controller
		// aco.setOrderResultURL(""); //付款完成後的結果參數 傳至前端用的
		aco.setClientBackURL("http://localhost:8080/appointment/paymentResults"); // 付完錢後的返回商店按鈕會到的網址

		String form = all.aioCheckOut(aco, null);
		System.out.println(form);
		result.put("tradeNo", TradeNo);
		result.put("schId", scheuleVO.getSchId());
		result.put("orderId", orderId);
		result.put("form", form);

		return result;
	}

	// 檢查 ECPay回傳的付款結果資訊
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
//				System.out.println(schId);
//				scheduleRepository.updateUnbooked(schId);
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

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

	public void startCheckECPayOrder(String tradeNo, Integer schId, Integer orderId) {
		final ScheduledFuture<?>[] scheduledFuture = new ScheduledFuture[1]; // 使用陣列來包裝，使它可被修改
		final ConcurrentHashMap<String, Integer> tradeNoCountMap = new ConcurrentHashMap<>(); // 用於存儲每個 tradeNo 的計數器
		tradeNoCountMap.putIfAbsent(tradeNo, 0);
		
		scheduledFuture[0] = scheduler.scheduleAtFixedRate(() -> {
			 // 每次執行時更新對應的 tradeNo 的計數器
            int currentCount = tradeNoCountMap.get(tradeNo);
            tradeNoCountMap.put(tradeNo, currentCount + 1); // 增加次數
			AllInOne all = new AllInOne("");
			QueryTradeInfoObj queryTradeInfoObj = new QueryTradeInfoObj();

			String MerchantID = "3002607";
			queryTradeInfoObj.setMerchantID(MerchantID); // 設定商戶ID
			queryTradeInfoObj.setMerchantTradeNo(tradeNo);

			String result = all.queryTradeInfo(queryTradeInfoObj);
			System.out.println("查詢結果: " + result);

			String[] strArr = result.split("&");
			Hashtable<String, String> ECPayReq = new Hashtable<>();

			for (String str : strArr) {
				String[] keyValue = str.split("=", 2);
				String key = keyValue[0];
				String Value = keyValue.length > 1 ? keyValue[1] : "";
				ECPayReq.put(key, Value);
			}
			String TradeStatus = ECPayReq.get("TradeStatus");
			System.out.println("第" + currentCount + "次執行:TradeStatus = " + ECPayReq.get("TradeStatus"));
			if ("1".equals(TradeStatus)) {
				scheduledFuture[0].cancel(false);
				return;
			}
			 if (!"0".equals(TradeStatus) || currentCount >= 6) {
				scheduleRepository.updateUnbooked(schId);
				ordersRepository.updateOrderStatus(0, orderId);
				scheduledFuture[0].cancel(false);
			}
		}, 2, 5, TimeUnit.MINUTES); //設定首次執行時延遲兩分鐘 後續每五分鐘執行一次一共執行六次
	}

	public Map<String, Object> checkPayment(Integer orderId) {
		OrdersVO ordersVO = ordersRepository.findByOrderId(orderId);
		Map<String, Object> result = new HashMap<>();
		if (ordersVO == null) {
			return result;
		}

		if (ordersVO.getStatus().equals(1)) {
			result.put("pay", "1");
			if (ordersVO.getPayMethod().equals(2)) {
				memberRepository.updatePoint(0, ordersVO.getMember().getMemId());
			}
		} else {
			result.put("pay", "0");
		}
		String timeslot = ordersVO.getSchedule().getTimeslot();
		Integer apptTime = 0;
		for (int i = 0; i < timeslot.length(); i++) {
			if (timeslot.charAt(i) != '0') {
				apptTime = i + 1;
				break; // 找到後就跳出迴圈
			}
		}
		PetVO petVO = ordersVO.getPet().get(0).getPet();

		OrderViewDTO orderViewDTO = new OrderViewDTO(ordersVO.getSchedule().getDate(), apptTime,
				ordersVO.getOnLocation(), ordersVO.getOffLocation(), ordersVO.getStaff().getStaffName(),
				ordersVO.getStaff().getStaffPhone(), ordersVO.getMember().getMemName(),
				ordersVO.getMember().getMemPhone(), petVO.getType(), petVO.getPetGender(), petVO.getPetName(),
				petVO.getWeight(), ordersVO.getNotes(), ordersVO.getPoint(), ordersVO.getPayment());

		result.put("order", orderViewDTO);
		return result;
	}

	public String getAmoute(String origin, String destination) {
//		final String API_KEY = "AIzaSyAJ4YeUWLDhM530z0_jUFfzYvSsQx_GVaU";
		final String ROUTES_API_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";
		final Integer STARTPRICE = 100;
		final Integer PRICEPERKM = 50;
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
			amoute = (int) Math.round(distance * PRICEPERKM + STARTPRICE);
			System.out.println("距離" + distance);
		}
		System.out.println("金額" + amoute);
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
		 Integer pageQty = (int)(total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
		 return pageQty;
	}
	public List<OrdersVO> getOrdersByPage(Integer memId, Integer page, Integer pageSize) {
	    int offset = (page - 1) * pageSize; //從第幾筆開始查
	    return ordersRepository.findOrdersByMemIdWithPagination(memId, offset, pageSize);
	}
	
	public List<OrdersVO> searchOrdersByKeyword(Integer memId, String keyword) {
	    return ordersRepository.findByMemberAndKeyword(memId, keyword);
	}
}
