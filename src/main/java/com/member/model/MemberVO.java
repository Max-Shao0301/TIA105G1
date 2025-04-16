package com.member.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.orders.model.OrdersVO;
import com.pet.model.PetVO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@DynamicInsert // 讓JAVA不會在SQL有預設值時 因為沒寫欄位而傳NULL過去
@Entity
@Table(name = "member")
public class MemberVO implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "mem_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memId;

	@Column(name = "mem_email")
	private String memEmail;

	@Column(name = "mem_password")
	private String memPassword;

	@Column(name = "mem_name")
	private String memName;

	@Column(name = "mem_phone")
	private String memPhone;

	@Column(name = "address")
	private String address;

	@Column(name="secret")
	private String secret;

	@Column(name = "point")
	private Integer point;

	@Column(name = "status")
	private Integer status;

	@Column(name = "create_time", updatable = false) // 讓更新時 建立時間不會被改到
	private LocalDateTime createTime;

	@UpdateTimestamp // 取得更新時的時間(JAVA傳過去)
	@Column(name = "update_time")
	private LocalDateTime updateTime;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<OrdersVO> orders;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PetVO> pet;

	public MemberVO() { // 必需有一個不傳參數建構子(JavaBean基本知識)
	}

	public Integer getMemId() {
		return memId;
	}

	public void setMemId(Integer memId) {
		this.memId = memId;
	}

	public String getMemEmail() {
		return memEmail;
	}

	public void setMemEmail(String memEmail) {
		this.memEmail = memEmail;
	}

	public String getMemPassword() {
		return memPassword;
	}

	public void setMemPassword(String memPassword) {
		this.memPassword = memPassword;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
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

	public List<OrdersVO> getOrders() {
		return orders;
	}

	public void setOrders(List<OrdersVO> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "MemberVO [memId=" + memId + ", memEmail=" + memEmail + ", memPassword=" + memPassword + ", memName="
				+ memName + ", memPhone=" + memPhone + ", address=" + address + ", secret=" + secret + ", point="
				+ point + ", status=" + status + ", createTime=" + createTime + ", updateTime=" + updateTime
				+ ", orders=" + orders + ", pet=" + pet + "]";
	}

}
