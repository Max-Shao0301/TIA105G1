package com.apply.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("applyService")
public class ApplyService {

    @Autowired
    private ApplyRepository applyRepository;

    public void addApply(ApplyVO applyVO) {
        applyRepository.save(applyVO);
    }

    //自訂方法，履歷審核
    public void updateApply(ApplyVO applyVO) {
        applyRepository.updateResults(applyVO.getResults(), applyVO.getApplyID());
    }

    public List<ApplyVO> getAll() {
        return applyRepository.findAll();
    }
}
