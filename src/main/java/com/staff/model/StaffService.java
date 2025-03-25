package com.staff.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("staffService")
public class StaffService {

	@Autowired
	StaffRepository repository;

	@Transactional
	public void addStaff(StaffVO staffVO) {
		repository.save(staffVO);
	}

	@Transactional
	public void updateStaff(StaffVO staffVO) {
		repository.save(staffVO);
	}
	
	@Transactional
	public void deleteStaff(Integer staffId) {
	    StaffVO staffVO = getOneStaff(staffId);
	    if (staffVO != null) {
	        staffVO.setStatus(0);
	        repository.save(staffVO);
	    }
	}

	public StaffVO getOneStaff(Integer staffId) {
		Optional<StaffVO> optional = repository.findById(staffId);
		return optional.orElse(null);
	}

	public List<StaffVO> getAll() {
		return repository.findAll();
	}

}
