package com.apply.controller;


import com.mail.MailService;
import com.apply.model.ApplyService;
import com.apply.model.ApplyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;


@Controller
public class ApplyController {

    @Autowired
    private ApplyService applyService;
    @Autowired
    private MailService mailService;

    @PostMapping("/updateResult")
    public String updateStatus(@RequestParam("applyId") Integer applyId, @RequestParam("results") Integer results) {
        String title = "寵愛牠平台審核通知";
        ApplyVO applyVO = new ApplyVO();
        applyVO.setApplyId(applyId);
        applyVO.setResults(results);
        applyService.updateApply(applyVO);
        ApplyVO vo = applyService.getOne(applyId);

        if (results == 1) { //如果返回1發送通過審核及帳密的信件
            //Collections.singleton()：這個方法會返回一個包含單個元素的不可變集合，這個集合大小為 1
            mailService.sendPlainText(Collections.singleton(vo.getApplyEmail()), title, vo.getApplyName() + " 先生/小姐，您好\n 您已通過平台審核成為寵愛牠接送服務人員\n登入帳號為您的電子信箱\n密碼為您的手機號碼\n\n歡迎您加入寵愛牠這個大家庭");
        } else if (results == 0) {//如果返回0發送位通過審核感謝信
            mailService.sendPlainText(Collections.singleton(vo.getApplyEmail()), title,  vo.getApplyName() + "先生/小姐，您好\n 感謝您提交申請參與本次審核，經過審慎評估，我們遺憾地通知您，本次未能通過審核\n我們誠摯感謝您的參與，也期待未來能有更多合作的機會。\n\n敬祝\n順心如意");
        }
        return "redirect:/admin/home/page";
    }




}
