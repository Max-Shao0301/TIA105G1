package com.staff.controller;


import com.staff.model.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/updateStaffStatus")
    public String updateStaffStatus(){




        return "redirect:/admin/home/page";
    }

}
