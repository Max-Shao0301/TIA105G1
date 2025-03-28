package com.schedule.model.dto;

import org.springframework.stereotype.Component;


public class StaffScheduleDTO {
	
	private Integer staffId;
	private Integer schId;
	private String staffName;
	private String staffPhone;
	private Integer staffGender;
	private String introduction;
	
	public StaffScheduleDTO (){
		
	}
	
	public StaffScheduleDTO(Integer staffId, Integer schId, String staffName, String staffPhone,
			Integer staffGender, String introduction) {
		super();
		this.staffId = staffId;
		this.schId = schId;
		this.staffName = staffName;
		this.staffPhone = staffPhone;
		this.staffGender = staffGender;
		this.introduction = introduction;
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public Integer getSchId() {
		return schId;
	}

	public void setSchId(Integer schId) {
		this.schId = schId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public String getStaffPhone() {
		return staffPhone;
	}

	public void setStaffPhone(String staffPhone) {
		this.staffPhone = staffPhone;
	}

	public Integer getStaffGender() {
		return staffGender;
	}

	public void setStaffGender(Integer staffGender) {
		this.staffGender = staffGender;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
}

