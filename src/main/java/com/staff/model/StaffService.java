package com.staff.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.springbootmail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enums.Status;

@Service("staffService")
public class StaffService {

	@Autowired
	StaffRepository repository;

	@Autowired
	private MailService mailService;

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
		String title = "寵愛牠服務人員停權通知";
	    StaffVO staffVO = getOneStaff(staffId);
	    if (staffVO != null) {

	        staffVO.setStatus(Status.close.getNumber());

			mailService.sendPlainText(Collections.singleton(staffVO.getStaffEmail()),
					title,
					staffVO.getStaffName() + " 先生/小姐，您好\n因您多次違反平台使用規則，將對您的帳號進行停權處理，即刻起無法登入系統\n，如您對此決定有任何疑問，或認為此處分有誤，請聯繫我們，並提供相關證明進行申訴\n聯繫信箱： max.shao@icloud.com\n感謝您的配合與理解\n寵愛牠平台方客服"
			);
	        repository.save(staffVO);
	    }
	}

	public StaffVO getOneStaff(Integer staffId) {
		Optional<StaffVO> optional = repository.findById(staffId);
		return optional.orElse(null);
	}
	
	public StaffVO getOneStaff(String email, String password) {
		Optional<StaffVO> optional = repository.findByLogin(email, password); 
		return optional.orElse(null);
	}
	
	public StaffVO getOneStaffByEmail(String email) {
		Optional<StaffVO> optional = repository.findByLoginByEmail(email); 
		return optional.orElse(null);
	}

	public List<StaffVO> getAll() {
		return repository.findAll();
	}

}