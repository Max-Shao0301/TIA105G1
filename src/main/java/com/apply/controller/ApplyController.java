package com.apply.controller;


import com.apply.model.ApplyService;
import com.apply.model.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ApplyController {

    @Autowired
    private ApplyService applyService;

    @PostMapping("/updateResult")
    public String updateStatus(@RequestParam("applyID") Integer applyID, @RequestParam("results") Integer results) {
        if (applyID == null) {
            throw new IllegalArgumentException("applyID cannot be null!");
        }
        ApplyVO applyVO = new ApplyVO();
        applyVO.setApplyID(applyID);
        applyVO.setResults(results);
        applyService.updateApply(applyVO);
        return "redirect:/admin/home/page";
    }




}
