package com.schedule.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.staff.model.StaffVO;

@Entity
@Table(name = "schedule")
public class ScheduleVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "sch_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer schId;

	@ManyToOne
	@JoinColumn(name = "staff_id")
	private StaffVO staffVO;

	@Column(name = "timeslot")
	private String timeslot;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "status")
	private Integer status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;
	
	public ScheduleVO() {
	}
	
	public Integer getSchId() {
		return schId;
	}
	public void setSchId(Integer schId) {
		this.schId = schId;
	}

	public StaffVO getStaffVO() {
		return staffVO;
	}
	public void setStaffVO(StaffVO staffVO) {
		this.staffVO = staffVO;
	}

	public String getTimeslot() {
		return timeslot;
	}
	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}
	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}

}
