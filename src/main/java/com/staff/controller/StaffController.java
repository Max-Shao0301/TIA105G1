package com.staff.controller;


import com.staff.model.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/updateStaffStatus")
    public String updateStaffStatus(@RequestParam("staff_id") Integer staffId){
        staffService.deleteStaff(staffId);

        return "redirect:/admin/home/page";
    }

}
