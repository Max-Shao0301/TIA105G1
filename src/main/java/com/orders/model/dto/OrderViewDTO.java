package com.orders.model.dto;

import java.time.LocalDate;

public class OrderViewDTO {
	
	private LocalDate date;
	private Integer apptTime;
	private String onLocation;
	private String offLocation;
	private String staffName;
	private String staffPhone;
	private String memName;
	private String memPhone;
	private String petType;
	private Integer petGender;
	private String petName;
	private Integer petWeight; 
	private String notes;
	private Integer point;
	private Integer payment;
	
	
	public OrderViewDTO(LocalDate date, Integer apptTime, String onLocation, String offLocation, String staffName,
			String staffPhone, String memName, String memPhone, String petType, Integer petGender, String petName,
			Integer petWeight, String notes, Integer point, Integer payment) {
		super();
		this.date = date;
		this.apptTime = apptTime;
		this.onLocation = onLocation;
		this.offLocation = offLocation;
		this.staffName = staffName;
		this.staffPhone = staffPhone;
		this.memName = memName;
		this.memPhone = memPhone;
		this.petType = petType;
		this.petGender = petGender;
		this.petName = petName;
		this.petWeight = petWeight;
		this.notes = notes;
		this.point = point;
		this.payment = payment;
	}
	public OrderViewDTO() {
		
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public Integer getApptTime() {
		return apptTime;
	}
	public void setApptTime(Integer apptTime) {
		this.apptTime = apptTime;
	}
	public String getOnLocation() {
		return onLocation;
	}
	public void setOnLocation(String onLocation) {
		this.onLocation = onLocation;
	}
	public String getOffLocation() {
		return offLocation;
	}
	public void setOffLocation(String offLocation) {
		this.offLocation = offLocation;
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
	public String getMemName() {
		return memName;
	}
	public void setMemName(String memName) {
		this.memName = memName;
	}
	public String getMemPhone() {
		return memPhone;
	}
	public void setMemPhone(String memPhone) {
		this.memPhone = memPhone;
	}
	public String getPetType() {
		return petType;
	}
	public void setPetType(String petType) {
		this.petType = petType;
	}
	public Integer getPetGender() {
		return petGender;
	}
	public void setPetGender(Integer petGender) {
		this.petGender = petGender;
	}
	public String getPetName() {
		return petName;
	}
	public void setPetName(String petName) {
		this.petName = petName;
	}
	public Integer getPetWeight() {
		return petWeight;
	}
	public void setPetWeight(Integer petWeight) {
		this.petWeight = petWeight;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getPayment() {
		return payment;
	}
	public void setPayment(Integer payment) {
		this.payment = payment;
	}
	
}
