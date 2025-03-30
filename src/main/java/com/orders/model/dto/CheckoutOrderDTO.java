package com.orders.model.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CheckoutOrderDTO {
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	private LocalDate date;
	 
	private Integer apptTime;
	private Integer schId;
	private Integer petId;
	private String onLocation;
	private String offLocation;
	private Integer point;
	private Integer payment;
	private Integer payMethod;
	private String notes;


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

	public Integer getSchId() {
		return schId;
	}

	public void setSchId(Integer schId) {
		this.schId = schId;
	}

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
