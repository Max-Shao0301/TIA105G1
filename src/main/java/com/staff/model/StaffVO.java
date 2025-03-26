package com.staff.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


import java.time.LocalDateTime;

import com.apply.model.ApplyVO;

@Entity
@Table(name = "staff")
public class StaffVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "staff_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer staffId;

	@OneToOne
	@JoinColumn(name = "apply_id")
	private ApplyVO apply;

	@Column(name = "staff_email")
	private String staffEmail;

	@Column(name = "staff_password")
	private String staffPassword;

	@Column(name = "staff_name")
	private String staffName;

	@Column(name = "staff_phone")
	private String staffPhone;

	@Column(name = "staff_gender")
	private Integer staffGender;

	@Column(name = "car_number")
	private String carNumber;

	@Column(name = "introduction")
	private String introduction;

	@Column(name = "status")
	private Integer status;

	@Column(name = "create_time")
	private LocalDateTime createTime;

	@Column(name = "update_time")
	private LocalDateTime updateTime;
	
	public StaffVO() {
	}

	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public ApplyVO getApply() {
		return apply;
	}

	public void setApply(ApplyVO apply) {
		this.apply = apply;
	}

	public String getStaffEmail() {
		return staffEmail;
	}

	public void setStaffEmail(String staffEmail) {
		this.staffEmail = staffEmail;
	}

	public String getStaffPassword() {
		return staffPassword;
	}

	public void setStaffPassword(String staffPassword) {
		this.staffPassword = staffPassword;
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

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
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
