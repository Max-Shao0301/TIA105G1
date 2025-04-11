package com.orders.model.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AppointmentTimeDTO {
	
	@NotNull(message = "日期不可為空")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
	@Future(message = "日期只能是今天或以後")
	private LocalDate date;
	
	@NotNull(message = "時間不可為空")
    @Positive(message = "時間必須是正整數")
	private Integer apptTime;

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
}
