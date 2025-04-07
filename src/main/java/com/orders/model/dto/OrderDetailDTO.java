package com.orders.model.dto;

import java.time.LocalDate;

public class OrderDetailDTO {
	private Integer orderId;
    private Integer orderStatus;
    private Integer star;
    private LocalDate scheduleTime;
    private String timeslot;
    private String createdTime;
    private String updateTime;
    private String onLocation;
    private String offLocation;
    private String carNumber;
    private String staffName;
    private String pet;
    private Integer payment;
    private Integer payMethod;
    private Integer point;
    private String notes;
    private String rating;
    private String appointmentTime;
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	public LocalDate getScheduleTime() {
		return scheduleTime;
	}
	public void setScheduleTime(LocalDate localDate) {
		this.scheduleTime = localDate;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
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
	public String getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public String getStaffName() {
		return staffName;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public String getPet() {
		return pet;
	}
	public void setPet(String pet) {
		this.pet = pet;
	}
	public Integer getPayment() {
		return payment;
	}
	public void setPayment(Integer payment) {
		this.payment = payment;
	}
	public Integer getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(Integer payMethod) {
		this.payMethod = payMethod;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}
	public String getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public Integer getStar() {
		return star;
	}
	public void setStar(Integer star) {
		this.star = star;
	}

}
