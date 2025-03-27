package com.orders.model.dto;

public class CheckoutOrderDTO {
	
	private Integer memId;
	private Integer staffId;
	private String onLocation;
	private String offLocation;
	private Integer point;
	private Integer payMethod;
	private String notes;
	
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public Integer getStaffId() {
		return staffId;
	}
	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
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
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
