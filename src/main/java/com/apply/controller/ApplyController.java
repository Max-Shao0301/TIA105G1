package com.apply.controller;


import com.springbootmail.MailService;
import com.apply.model.ApplyService;
import com.apply.model.ApplyVO;
import com.staff.model.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;


@Controller
public class ApplyController {

    @Autowired
    private ApplyService applyService;
    @Autowired
    private MailService mailService;

    @Autowired
    private StaffService staffService;

    @PostMapping("/updateResult")
    public String updateStatus(@RequestParam("applyId") Integer applyId, @RequestParam("results") Integer results) {
        String title = "寵愛牠平台審核通知";
        ApplyVO applyVO = new ApplyVO();
        applyVO.setApplyId(applyId);
        applyVO.setResults(results);
        applyService.updateApply(applyVO);

        return "redirect:/admin/home/page";
    }

    @GetMapping("/joinus")
    public String joinus(Model model) {

        return "/front-end/joinus";
    }




}
