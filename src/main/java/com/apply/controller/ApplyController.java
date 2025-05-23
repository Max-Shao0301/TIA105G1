package com.apply.controller;


import com.apply.model.ApplyService;
import com.apply.model.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
public class ApplyController {

    @Autowired
    private ApplyService applyService;


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
        model.addAttribute("ApplyVO", new ApplyVO());
        return "front-end/joinus";
    }

    @PostMapping("/insert/apply")
    public String insertApply(ApplyVO applyVO, BindingResult result, ModelMap model,
                              @RequestParam("license") MultipartFile [] parts) throws IOException {
        for (MultipartFile multipartFile : parts) {
            byte[] licensePhoto = multipartFile.getBytes();
            applyVO.setLicense(licensePhoto);
        }



        applyService.addApply(applyVO);
        List<ApplyVO> applyList = applyService.getAll();
        model.addAttribute("applyList", applyList);
        return "redirect:/";
    }

}
