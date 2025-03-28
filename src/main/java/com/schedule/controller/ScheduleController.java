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

@Controller
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    @GetMapping("/staff/schedule")
    public String staffSchedule(Model model) {
    	
        model.addAttribute("scheduleVO", new ScheduleVO());  // 避免NullPointerException錯誤
        model.addAttribute("schedules", scheduleService.getScheduleByStaff(1));
        return "back-end/staff/schedule";
        
    }

    @PostMapping("/staff/schedule/insert")
    public String addSchedule(@ModelAttribute ScheduleVO scheduleVO) {
    	
        StaffVO staffVO = new StaffVO();
        staffVO.setStaffId(1);
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

