package com.apply.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public ApplyVO getOne(Integer applyID){
        Optional<ApplyVO> optional = applyRepository.findById(applyID);
        return optional.orElse(null);
    }

    public List<ApplyVO> getAll() {
        return applyRepository.findAll();
    }
}
