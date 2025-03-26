package com.apply.model;

import com.springbootmail.MailService;
import com.staff.model.StaffService;
import com.staff.model.StaffVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service("applyService")
public class ApplyService {

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private StaffService staffService;

    //新增
    public void addApply(ApplyVO applyVO) {
        applyRepository.save(applyVO);
    }

    //自訂方法，履歷審核
    @Transactional
    public void updateApply(ApplyVO applyVO) {
        String title = "寵愛牠平台審核通知";
        applyRepository.updateResults(applyVO.getResults(), applyVO.getApplyId());
        ApplyVO vo = getOne(applyVO.getApplyId());

        if (applyVO.getResults() == 1){//如果返回0發送位通過審核感謝信
            mailService.sendPlainText(Collections.singleton(vo.getApplyEmail()), title, vo.getApplyName() + " 先生/小姐，您好\n 您已通過平台審核成為寵愛牠接送服務人員\n登入帳號為您的電子信箱\n密碼預設為您的手機號碼\n歡迎您加入寵愛牠這個大家庭\n寵愛牠管理平台");
            StaffVO staffVO = new StaffVO();  //通過後將履歷轉給員工進行保存
            staffVO.setApply(vo);
            staffVO.setStaffEmail(vo.getApplyEmail());
            staffVO.setStaffPassword(vo.getApplyPhone());
            staffVO.setStaffName(vo.getApplyName());
            staffVO.setStaffPhone(vo.getApplyPhone());
            staffVO.setStaffGender(vo.getApplyGender());
            staffVO.setCarNumber(vo.getCarNumber());
            staffVO.setIntroduction(vo.getIntroduction());

            staffService.addStaff(staffVO);

        } else if (applyVO.getResults() == 0) {//如果返回0發送位通過審核感謝信
            mailService.sendPlainText(Collections.singleton(vo.getApplyEmail()), title,  vo.getApplyName() + " 先生/小姐，您好\n 感謝您提交申請參與本次審核，經過審慎評估，我們遺憾地通知您，本次未能通過審核\n我們誠摯感謝您的參與，也期待未來能有更多合作的機會。\n敬祝\n順心如意\n寵愛牠管理平台");
        }
    }

    public ApplyVO getOne(Integer applyId){
        Optional<ApplyVO> optional = applyRepository.findById(applyId);
        return optional.orElse(null);
    }

    public List<ApplyVO> getAll() {
        return applyRepository.findAll();
    }
}
