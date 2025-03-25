package com.orders.model;

import java.time.LocalDateTime;

import com.schedule.model.ScheduleVO;
import com.staff.model.StaffVO;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.member.model.MemberVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@DynamicInsert // 讓JAVA不會在SQL有預設值時 因為沒寫欄位而傳NULL過去
@Entity
@Table(name = "orders")
public class OrdersVO implements java.io.Serializable {

	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;

	@ManyToOne
	@JoinColumn(name = "mem_id", referencedColumnName = "mem_id")
	private MemberVO member;

	@ManyToOne
	@JoinColumn(name="sch_id", referencedColumnName = "sch_id")
	private ScheduleVO schedule;

	@ManyToOne
	@JoinColumn(name="staff_id", referencedColumnName = "staff_id")
	private StaffVO staff;

	@Column(name = "on_location")
	private String onLocation;

	@Column(name = "off_location")
	private String offLocation;

	@Column(name = "point")
	private Integer point;

	@Column(name = "payment")
	private Integer payment;

	@Column(name = "pay_method")
	private Integer payMethod;

	@Column(name = "notes")
	private String notes;

	@Column(name = "status")
	private Integer status;

	@Column(name = "star")
	private Integer star;

	@Column(name = "rating")
	private String rating;

	@Column(name = "picture")
	private byte[] picture;

	@Column(name = "create_time", updatable = false)
	private LocalDateTime createTime;

	@UpdateTimestamp // 取得更新時的時間(JAVA傳過去)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

//	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<OrderPetVO> pet;

	public OrdersVO() {

	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStar() {
		return star;
	}

	public void setStar(Integer star) {
		this.star = star;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
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
