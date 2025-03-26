package com.schedule.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enums.*;

@Service("scheduleService")
public class ScheduleService {

	@Autowired
	ScheduleRepository repository;

	@Transactional
	public void addSchedule(ScheduleVO scheduleVO) {
		if(repository.checkScheduleUnique(scheduleVO.getStaffVO().getStaffId(), scheduleVO.getDate(), scheduleVO.getTimeslot())<=0) {
			repository.save(scheduleVO);
		}
	}

	@Transactional
	public void updateSchedule(ScheduleVO scheduleVO) {
		repository.save(scheduleVO);
	}

	@Transactional
	public void deleteSchedule(Integer schId) {
	    ScheduleVO scheduleVO = getOneSchedule(schId);
	    if (scheduleVO != null) {
	        scheduleVO.setStatus(Status.close.getNumber());
	        repository.save(scheduleVO);
	    }
	}

	public ScheduleVO getOneSchedule(Integer schId) {
		Optional<ScheduleVO> optional = repository.findById(schId);
		return optional.orElse(null);
	}

	public List<ScheduleVO> getAll() {
		return repository.findAll();
	}
	
}
