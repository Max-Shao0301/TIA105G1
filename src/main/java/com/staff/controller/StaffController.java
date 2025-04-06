package com.staff.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        	
            session.setAttribute("staffName", staffVO.getStaffName());
            session.setAttribute("staffId", staffVO.getStaffId());       
            return "redirect:/staff/home";
            
        } else {
        	
            return "/staff/login";
            
        }
    }
    
    @PostMapping("/staff/logout")
    public String logout(HttpSession session) {
    	
        session.invalidate();
        return "redirect:/staff/login";
        
    }

    //staff 首頁顯示的是他的訂單
    @GetMapping("/staff/home")
    public String staffHome(Model model) {
    	
//        model.addAttribute("OrderVO", new OrdersVO());  // 避免NullPointerException錯誤
//        model.addAttribute("orders", ordersService.getAll());
        return "redirect:/staff/schedule";

        
    }
    
    
    
}
