package com.staff.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrdersService ordersService;
    
    @Autowired
    private JavaMailSender mailSender;

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

                             Model model, HttpSession session, HttpServletRequest request) {
    	
        StaffVO staffVO = staffService.getOneStaff(staffEmail,staffPassword);

                             HttpSession session) {

        StaffVO staffVO = staffService.getOneStaff(staffEmail, staffPassword);

        //比對雜湊登入
//        StaffVO staffVO = staffService.getOneStaff(staffEmail);
//        if (staffVO != null && passwordEncoder.matches(staffPassword, staffVO.getStaffPassword())) {
//            session.setAttribute("staffName", staffVO.getStaffName());
//            session.setAttribute("staffId", staffVO.getStaffId());
//            return "redirect:/staff/home";
//        } else {
//            return "/back-end/staff/login";
//        }


        if (staffVO != null && staffVO.getStaffPassword().equals(staffPassword)) {
        	
            session.invalidate();
            session = request.getSession(true); //避免 session ID被取得
            session.setAttribute("staffName", staffVO.getStaffName());
            session.setAttribute("staffId", staffVO.getStaffId());       
            return "redirect:/staff/home";
            
        } else {
        	

        	model.addAttribute("error", "帳號或密碼錯誤"); 
            return "/back-end/staff/login"; 

            return "/staff/login";

            
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
    public String forgotPassword(@RequestParam("staffEmailForget") String staffEmail, Model model, HttpSession session) {

        int passwordCode = (int) (Math.random() * 9000 + 1000);

        // 將驗證碼和電子郵件存儲在 Session 中，增加一個新的flag參數
        session.setAttribute("passwordCode", passwordCode);
        session.setAttribute("forgotPasswordEmail", staffEmail);
    	session.setAttribute("resetPasswordFlag", "true");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(staffEmail);
        message.setSubject("你於寵愛他申請重設密碼，" + passwordCode);

        // 建立完整的 URL，不包含查詢參數
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/staff/login/forgotSetPassword")
                .queryParam("reset", "true")
                .toUriString();
        message.setText("請用下列網址重設:\n" + baseUrl);

        try {
            mailSender.send(message);
            System.err.println("信件發送成功");
            model.addAttribute("error", "已寄送新亂碼密碼，請至信箱查收，並在30分鐘內重設");
        } catch (MailException e) {
            System.err.println("信件發送失敗: " + e.getMessage());
            model.addAttribute("error", "信件發送失敗，請稍後再試");
        }

        return "/back-end/staff/login";
    }
    
    //重設密碼
    @PostMapping("/staff/login/forgotSetPassword")
    public String forgotSetPassword(@RequestParam("reset") String reset, Model model, HttpSession session) {
    	
    	String resetPasswordFlag = (String) session.getAttribute("resetPasswordFlag");
    	
    	if(resetPasswordFlag == null) {
    		
    		 model.addAttribute("error", "重設密碼的資訊已過期，請重新發送請求。");
    		 return "/back-end/staff/login";
    	}
    	
        Integer passwordCode = (Integer) session.getAttribute("passwordCode");
        String staffEmail = (String) session.getAttribute("forgotPasswordEmail");

        if (passwordCode == null || staffEmail == null) {
        	
            model.addAttribute("error", "重設密碼的資訊已過期，請重新發送請求。");
            return "/back-end/staff/login";
            
        }

        // 使用驗證碼和電子郵件進行密碼重設的邏輯
        model.addAttribute("passwordCode", passwordCode);
        model.addAttribute("staffEmail", staffEmail);

        // 重設完密碼後，請記得清除 Session 中的資料
        session.removeAttribute("passwordCode");
        session.removeAttribute("forgotPasswordEmail");
    	session.removeAttribute("resetPasswordFlag");
        return "/back-end/staff/login";
        
    }    
       
    //設定服務人員資料
    @GetMapping("/staff/setting")
    public String staffSettingPage(Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");

        if (staffId == null) {

            return "redirect:/staff/login";

        }

        StaffVO staff = staffService.getOneStaff(staffId);
        model.addAttribute("StaffVO", staff);
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
    public String updateStaffInfo(@ModelAttribute StaffVO staffVO, HttpSession session) {
    	
        Integer staffId = (Integer) session.getAttribute("staffId");
        
        if (staffId == null || !(staffId.equals(staffVO.getStaffId()))) {
        	
        	return "redirect:/staff/login";

        }
        
        StaffVO staffVODB = staffService.getOneStaff(staffId);
    	staffVODB.setStaffName(staffVO.getStaffName());
    	staffVODB.setCarNumber(staffVO.getCarNumber());
    	staffVODB.setStaffPhone(staffVO.getStaffPhone());
    	staffVODB.setIntroduction(staffVO.getIntroduction());
        staffService.updateStaff(staffVODB);
        session.setAttribute("staffName", staffVODB.getStaffName());
        return "redirect:/staff/home";
        
    }
    
    
    @GetMapping("/staff/updateEmail")
    public String fupdateStaffEmailPage() {
    	
        return "redirect:/staff/login";
        
    }
    
    
    @PostMapping("/staff/updateEmail")
    public String updateStaffEmail(@RequestParam("staffEmail") String staffEmail, Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");
        StaffVO staffVO = staffService.getOneStaff(staffId);

        if (staffId == null || staffVO == null) {
        	
            return "redirect:/staff/login";
            
        }
        
        // 信箱驗證
        Matcher matcher = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(staffEmail);
        if (staffEmail == null || staffEmail.isEmpty() ||!matcher.matches()) {
        	
        	model.addAttribute("error", "信箱格式錯誤"); 
        	model.addAttribute("errorEmail", "信箱格式錯誤"); 
        	model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }

        staffVO.setStaffEmail(staffEmail);
        staffService.updateStaff(staffVO);
        model.addAttribute("error", "信箱重設成功，請重新登入"); 
        return "/back-end/staff/login";
        
    }
    
    @GetMapping("/staff/updatePassword")
    public String updateStaffPasswordPage() {
    	
        return "redirect:/staff/login";
        
    }
    
    @PostMapping("/staff/updatePassword")
    public String updateStaffPassword(@RequestParam("newPassword") String newPassword,
                                      @RequestParam("checkNewPassword") String checkNewPassword,
                                      Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");
        StaffVO staffVO = staffService.getOneStaff(staffId);

        if (staffId == null || staffVO == null) {
        	
        	return "redirect:/staff/login";
            
        }

     // 密碼驗證
        Matcher matcher = Pattern.compile("^[a-zA-Z0-9!@#$%^&*]{6,20}$").matcher(checkNewPassword);
        if (newPassword == null || newPassword.isEmpty() ||!matcher.matches()) {
        	
            model.addAttribute("error", "密碼格式有誤，至少需設置六位數");
            model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }else if(!newPassword.equals(checkNewPassword)) {
        	
            model.addAttribute("error", "兩次密碼不一致");
            model.addAttribute("StaffVO", staffVO);
            return "/back-end/staff/setting";
            
        }
        	
        staffVO.setStaffPassword(newPassword);
        staffService.updateStaff(staffVO);
        model.addAttribute("error", "密碼重設成功，請重新登入");
        return "/back-end/staff/login";
        
    }
    
    //staff 首頁顯示的是他的訂單
    @GetMapping("/staff/home")
    public String staffHome(Model model, HttpSession session) {

        Integer staffId = (Integer) session.getAttribute("staffId");

        if (staffId == null) {
        	
        	return "redirect:/staff/login";

        }
        
        model.addAttribute("OrderVO", new OrdersVO());
        model.addAttribute("orders", ordersService.getstaffOrder(staffId));
        return "/back-end/staff/staffhome";
        
    }
    
    //訂單結案
    @PostMapping("/staff/finishOrder")
    public String finishStaffOrder(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("image") MultipartFile image,
            HttpSession session) {

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
