package com.schedule.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enums.Status;
import com.schedule.model.dto.StaffScheduleDTO;

@Service("scheduleService")
public class ScheduleService {

	@Autowired
	ScheduleRepository repository;

	@Transactional
	public boolean addSchedule(ScheduleVO scheduleVO) {
		if(repository.checkScheduleUnique(scheduleVO.getStaffVO().getStaffId(), scheduleVO.getDate(), scheduleVO.getTimeslot())<=0) {
			
			scheduleVO.setStatus(Status.open.getNumber());
			repository.save(scheduleVO);
			return true;
			
		}else {
			
			return false;
			
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
	

	public List<ScheduleVO> getScheduleByStaff(Integer staffId) {
		return repository.findByStaffOn(staffId);
	}
 
  
	public List<StaffScheduleDTO> findByBookableStaff(LocalDate date,Integer apptttime ){
		
		List<ScheduleVO> bookableSchedule = repository.findByBookableStaff(date, apptttime);
		List<StaffScheduleDTO> staffScheduleList = bookableSchedule.stream()
				.map(schedule -> new StaffScheduleDTO(
						schedule.getStaffVO().getStaffId(),
						schedule.getSchId(),
						schedule.getStaffVO().getStaffName(),
						schedule.getStaffVO().getStaffPhone(),
						schedule.getStaffVO().getStaffGender(),
						schedule.getStaffVO().getIntroduction()
				))
				.collect(Collectors.toList());
		return staffScheduleList;

	}

}
