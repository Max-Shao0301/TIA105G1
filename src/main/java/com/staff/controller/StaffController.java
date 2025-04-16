package com.staff.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.staff.model.*;
import com.orders.model.OrdersService;
import com.orders.model.OrdersVO;

@Controller
public class StaffController {

    @Autowired
    private StaffService staffService;
    
    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    //排程
    private Map<String, Map<String, Object>> resetPasswordData = new HashMap<>();
    private final ScheduledExecutorService scheduleDeleteMap = Executors.newScheduledThreadPool(1);
    
    //google map api
    @Value("${google.maps.api.key}")
    private String mapApiKey;
    
    @PostMapping("/updateStaffStatus")
    public String updateStaffStatus(@RequestParam("staff_id") Integer staffId){
    	
        staffService.deleteStaff(staffId);
        return "redirect:/admin/home/page";
        
    }
    
    @GetMapping("/staff/login")
    public String staffLoginPage() {
    	
        return "/back-end/staff/login";
        
    }
    
    @PostMapping("/staff/login")
    public String staffLogin(@RequestParam("staffEmail") String staffEmail,
                             @RequestParam("staffPassword") String staffPassword,
                             RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request) {
    	
        StaffVO staffVO = staffService.getOneStaff(staffEmail,staffPassword);

        if (staffVO != null && staffVO.getStaffPassword().equals(staffPassword)) {
        	
            session.invalidate();
            session = request.getSession(true); //避免 session ID被取得
            session.setAttribute("staffName", staffVO.getStaffName());
            session.setAttribute("staffId", staffVO.getStaffId());       
            return "redirect:/staff/home";
            
        } else {
            
            redirectAttributes.addFlashAttribute("error", "帳號或密碼錯誤");
            return "redirect:/staff/login";
            
        }
        
    }
    
    @PostMapping("/staff/logout")
    public String logout(HttpSession session) {
    	
        session.invalidate();
        return "redirect:/staff/login";
        
    }
    
    //忘記密碼
    @GetMapping("/staff/forgotPassword")
    public String forgotPasswordPage() {

        return "redirect:/staff/login";
        
    }
    
    
    @PostMapping("/staff/forgotPassword")
    public String forgotPassword(@RequestParam("staffEmailForget") String staffEmail, RedirectAttributes redirectAttributes)  {
        
    	if((staffService.getOneStaffByEmail(staffEmail.trim())) == null) {
    		
    		redirectAttributes.addFlashAttribute("error", "找不到帳號，請再次確認帳號");
            return "redirect:/staff/login";
            
    	}
    	
    	//產生亂數
        int codeLong = 20;
        int resatTime = 10;
        String codeValue = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";
		String randCode = "";
        Random random = new Random();
        
        for (int i = 0; i < codeLong; i++) {
        	
        	randCode += codeValue.charAt(random.nextInt(codeValue.length()));
        	
        }
        
        //存入亂數
        Map<String, Object> codeInfo = new HashMap<>();
        codeInfo.put("staffEmail", staffEmail.trim());       
        codeInfo.put("resetEndTime", LocalDateTime.now().plusMinutes(resatTime));
        resetPasswordData.put(randCode, codeInfo);
        
        //設定十分鐘移除
        scheduleDeleteMap.schedule(new MyTask(randCode), resatTime, TimeUnit.MINUTES);
        
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(staffEmail);
        message.setSubject("你於寵愛他申請重設密碼，若沒有要重設密碼請忽視");
        message.setText("請用下列網址重設:\n" + 
	        		ServletUriComponentsBuilder.fromCurrentContextPath()
			        .path("/staff/login/forgotSetPassword")
			        .queryParam("value", randCode)
			        .toUriString()
        		);
        
        try {
        	
            mailSender.send(message);
    		redirectAttributes.addFlashAttribute("ok", "已寄送重設密碼連結，請至信箱查收，並在10分鐘內重設");
    		
        } catch (MailException e) {
        	
            System.err.println("信件發送失敗: " + e.getMessage());
    		redirectAttributes.addFlashAttribute("error", "信件發送失敗，請稍後再試");
        }

        return "redirect:/staff/login";
        
    }
    
    //移除Map排程
    class MyTask implements Runnable {

        private final String code;

        public MyTask(String code) {
        	
            this.code = code;
            
        }

        @Override
        public void run() {
        	
            resetPasswordData.remove(code);
            
        }
        
    }
    
    
    @GetMapping("/staff/login/forgotSetPassword")
    public String forgotSetPasswordPage(@RequestParam("value") String randCode, HttpSession session) {

    	Map<String, Object> codeInfo = resetPasswordData.get(randCode);

    	if(codeInfo == null) {
    		
	   		 return "redirect:/staff/login";
 		 
    	}

    	LocalDateTime resetEndTime = (LocalDateTime) codeInfo.get("resetEndTime");
        String staffEmail = (String) codeInfo.get("staffEmail");
        StaffVO staffVO = staffService.getOneStaffByEmail(staffEmail);
        
    	if(!(LocalDateTime.now().isBefore(resetEndTime)) || staffVO == null) {
    		
	   		 return "redirect:/staff/login";
		 
    	}
    	
		session.setAttribute("changestaffId", staffVO.getStaffId());
        return "/back-end/staff/setpassword";
        
    }
    
    
    //重設密碼
    @PostMapping("/staff/login/forgotSetPassword")
    public String forgotSetPassword(@RequestParam("checkNewPassword") String staffPassword,
    		 						HttpSession session, RedirectAttributes redirectAttributes, Model model) {
    	
    	int staffId = (int) session.getAttribute("changestaffId");
    	
    	StaffVO staffVO = staffService.getOneStaff(staffId);
    	
    	if(staffVO == null) {
    		
    		redirectAttributes.addFlashAttribute("error", "請再確認信箱");
   		 	return "redirect:/staff/login";
   		 
    	}
    	

    	if(!(staffPassword.trim().matches("^[a-zA-Z0-9!@#$%^&*]{6,20}$"))) {
    		
    		model.addAttribute("error", "密碼格式有誤，至少需設置六位大小寫英文數字");
    		session.setAttribute("changestaffId", staffId);
    		return "/back-end/staff/setpassword";
    		
    	}
   	
    	staffVO.setStaffPassword(staffPassword);
    	staffService.updateStaff(staffVO);
		redirectAttributes.addFlashAttribute("ok", "密碼已經重設密碼，請重新登入。");
        return "redirect:/staff/login";
        
    }    
       
    //設定服務人員資料
    @GetMapping("/staff/setting")
    public String staffSettingPage(Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");

        if (staffId == null) {

            return "redirect:/staff/login";

        }

        StaffVO staffVO = staffService.getOneStaff(staffId);
        model.addAttribute("StaffVO", staffVO);
        return "/back-end/staff/setting";

    }

    
    @PostMapping("/staff/setting")
    public String staffSetting(Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");

        if (staffId == null) {
        	
        	 return "redirect:/staff/login";

        }

        StaffVO staff = staffService.getOneStaff(staffId);

        
        model.addAttribute("StaffVO", staff); 
        return "/back-end/staff/setting";
        
    }
    
    //修改資料
    @PostMapping("/staff/update")
    public String updateStaffInfo(@ModelAttribute StaffVO staffVO,
    							HttpSession session, RedirectAttributes redirectAttributes) {
    	
        Integer staffId = (Integer) session.getAttribute("staffId");
        boolean error = true;
        
        if (staffId == null || !(staffId.equals(staffVO.getStaffId()))) {
        	
        	return "redirect:/staff/login";

        }
        
        StaffVO staffVODB = staffService.getOneStaff(staffId);
        
    	if (staffVO.getStaffName().trim() != "") {
    		
        	staffVODB.setStaffName(staffVO.getStaffName().trim());
        	
    	} else {
    		
			redirectAttributes.addFlashAttribute("nameError", "姓名不得為空值!");
			error = true;
			
    	}
        
    	
    	if (staffVO.getStaffPhone().trim().matches("\\d{10}")) {
    		
        	staffVODB.setStaffPhone(staffVO.getStaffPhone().trim());
        	
    	} else {
    		
			redirectAttributes.addFlashAttribute("phoneError", "電話號碼必須為十個數字！");
			error = true;
			
    	}
    	
    	if (staffVO.getCarNumber().trim().matches("^[A-Z0-9]+$")) {
    		
        	staffVODB.setCarNumber(staffVO.getCarNumber().trim());
        	
    	} else {
    		
    		redirectAttributes.addFlashAttribute("carNumberError", "車牌號僅能輸入大寫英及數字");
    		error = true;
    		
    	}
    	
    	staffVODB.setIntroduction(staffVO.getIntroduction().trim());
        staffService.updateStaff(staffVODB);
        session.setAttribute("staffName", staffVODB.getStaffName().trim());
        
    	if (error) {
    		
    		return "redirect:/staff/setting";
    		
    	}

        return "redirect:/staff/home";
        
    }
    
    
    @GetMapping("/staff/updateEmail")
    public String fupdateStaffEmailPage() {
    	
        return "redirect:/staff/setting";
        
    }
    
    
    @PostMapping("/staff/updateEmail")
    public String updateStaffEmail(@RequestParam("staffEmail") String staffEmail,
    								RedirectAttributes redirectAttributes, HttpSession session, Model model) {

        Integer staffId = (Integer) session.getAttribute("staffId");
        StaffVO staffVO = staffService.getOneStaff(staffId);

        if (staffId == null || staffVO == null) {
        	
            return "redirect:/staff/login";
            
        }
        
        if (staffEmail == null || staffEmail.isEmpty() ||!(staffEmail.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
        	
        	model.addAttribute("error", "設定信箱格式錯誤"); 
        	model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }

        if(staffService.getOneStaffByEmail(staffEmail) == null) {
        	
            staffVO.setStaffEmail(staffEmail);
            staffService.updateStaff(staffVO);
            redirectAttributes.addFlashAttribute("ok", "信箱重設成功，請重新登入"); 
            return "redirect:/staff/login";
        	
        }else {
        	
			redirectAttributes.addFlashAttribute("error", "該信箱無法註冊請重新設定"); 
        	return "redirect:/staff/setting";
        	
        }
        

    }
    
    @GetMapping("/staff/updatePassword")
    public String updateStaffPasswordPage() {
    	
        return "redirect:/staff/login";
        
    }
    
    @PostMapping("/staff/updatePassword")
    public String updateStaffPassword(@RequestParam("newPassword") String newPassword,
                                      @RequestParam("checkNewPassword") String checkNewPassword,
                                      Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        Integer staffId = (Integer) session.getAttribute("staffId");
        StaffVO staffVO = staffService.getOneStaff(staffId);

        if (staffId == null || staffVO == null) {
        	
        	return "redirect:/staff/login";
            
        }

     // 密碼驗證
        if (newPassword == null || newPassword.isEmpty() ||!(checkNewPassword.trim().matches("^[a-zA-Z0-9!@#$%^&*]{6,20}$"))) {
        	
            model.addAttribute("error", "密碼格式有誤，至少需設置六位大小寫英文數字");
            model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }else if(!newPassword.equals(checkNewPassword)) {
        	
            model.addAttribute("error", "兩次密碼不一致");
            model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }
        	
        staffVO.setStaffPassword(newPassword);
        staffService.updateStaff(staffVO);
		redirectAttributes.addFlashAttribute("ok", "密碼重設成功，請重新登入");
        return "redirect:/staff/login";
        
    }
    
    //staff 首頁顯示的是他的訂單
    @GetMapping("/staff/home")
    public String staffHome(Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");

        if (staffId == null) {
        	
        	return "redirect:/staff/login";

        }
        
        model.addAttribute("mapApiKey", mapApiKey);
        model.addAttribute("OrderVO", new OrdersVO());
        model.addAttribute("orders", ordersService.getstaffOrder(staffId));
        return "/back-end/staff/staffhome";
        
    }
    
    //訂單結案
    @PostMapping("/staff/endOrders")
    public String endOrders(@RequestParam("orderId") Integer orderId,@RequestParam("image") MultipartFile image,HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");
        OrdersVO ordersVO = ordersService.getOneOrder(orderId);
        
        if (staffId == null) {
        	
            return "redirect:/staff/login";
            
        }
        
        try {
        	
			ordersVO.setPicture(image.getBytes());
	        ordersVO.setStatus(2);
	        ordersService.updateOrders(ordersVO);
	        
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}

        return "redirect:/staff/home";
    }
    
}