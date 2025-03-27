package com.pet.model;


import java.time.LocalDateTime;

import com.member.model.MemberVO;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@DynamicInsert
@Entity
@Table(name = "pet")
public class PetVO implements java.io.Serializable {
    
	private static final long serialVersionUID = 1L;

    public PetVO() {
    	
    }
 
    @Id
    @Column(name = "pet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer petId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_id")
    private MemberVO member;
    
    @Column(name = "pet_name")
    private String petName;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "pet_gender")
    private Integer petGender;
    
    @Column(name = "weight")
    private Integer weight;
    
    @Column(name = "status")
    private Integer status;

    @Column(name="create_time",updatable = false)
    private LocalDateTime createTime;
    
	
	@UpdateTimestamp //取得更新時的時間(JAVA傳過去)
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
     
    public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public Integer getPetGender() {
		return petGender;
	}

	public void setPetGender(Integer petGender) {
		this.petGender = petGender;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
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
