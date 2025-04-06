package com.orders.model.dto;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CheckoutOrderDTO {
	
	@JsonFormat(pattern = "yyyy/MM/dd")
	@NotNull(message = "date不得為空")
	@FutureOrPresent(message = "日期只能是今天或以後")
	private LocalDate date;
	
	@Positive(message = "apptTime必須是正數")
	@NotNull(message = "apptTime不得為空")
	private Integer apptTime;
	
	@Positive
	@NotNull(message = "schId不得為空")
	private Integer schId;
	
	@Positive
	@NotNull(message = "petId不得為空")
	private Integer petId;
	
	@NotNull(message = "onLocation不得為空")
	private String onLocation;
	
	@NotNull(message = "offLocation不得為空")
	private String offLocation;
	
	@Min(value = 0, message = "point必須大於或等於0")
	@NotNull(message = "point不得為空")
	private Integer point;
	
	@NotNull(message = "payment不得為空")
	private Integer payment;
	
	@Range(min = 0, max = 1 , message = "payMethod必須是0或1")
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
