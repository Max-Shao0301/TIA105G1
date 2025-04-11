package com.schedule.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.schedule.model.*;
import com.staff.model.StaffVO;

import jakarta.servlet.http.HttpSession;

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    @GetMapping("/staff/schedule")
    public String staffSchedule(Model model, HttpSession session) {
    	
        model.addAttribute("scheduleVO", new ScheduleVO());  // 避免NullPointerException錯誤
    	Integer staffId = (Integer) session.getAttribute("staffId");
    	
        if (staffId != null) {
        	
            model.addAttribute("schedules", scheduleService.getScheduleByStaff(staffId));
            
        } else {
        	
            return "redirect:/staff/login";
            
        }
        
        return "back-end/staff/schedule";
        
    }

    @PostMapping("/staff/schedule/insert")
    public String addSchedule(@ModelAttribute ScheduleVO scheduleVO,HttpSession session) {
    	
        StaffVO staffVO = new StaffVO();
        staffVO.setStaffId((Integer) session.getAttribute("staffId"));
        scheduleVO.setStaffVO(staffVO);
        scheduleService.addSchedule(scheduleVO);
        
        return "redirect:/staff/schedule";
        
    }

    @PostMapping("/staff/schedule/delete")
    public String deleteSchedule(@RequestParam("schId") Integer schId) {
    	
    	scheduleService.deleteSchedule(schId);
    	
        return "redirect:/staff/schedule";
        
    }
}

