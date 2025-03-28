package com.apply.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@DynamicInsert // 讓JAVA不會在SQL有預設值時 因為沒寫欄位而傳NULL過去
@Entity
@Table(name = "apply")
public class ApplyVO implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public ApplyVO() {
    }

    @Id
    @Column(name="apply_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applyId;

    @Column(name="apply_email")
    private String applyEmail;

    @Column(name="apply_name")
    private String applyName;

    @Column(name="apply_phone")
    private String applyPhone;

    @Column(name="apply_gender")
    private Integer applyGender;

    @Lob //JPA 會知道這是一個大物件欄位，並在適當的資料庫型態（如 BLOB）中儲存它
    @Column(name="license")
    private byte[] license;

    @Column(name="car_number")
    private String carNumber;

    @Column(name="introduction")
    private String introduction;

    @Column(name="results")
    private Integer results;

    @Column(name="create_time")
    private LocalDateTime createTime;

    @Column(name="review_time")
    private LocalDateTime reviewTime;

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    public String getApplyEmail() {
        return applyEmail;
    }

    public void setApplyEmail(String applyEmail) {
        this.applyEmail = applyEmail;
    }

    public String getApplyName() {
        return applyName;
    }

    public void setApplyName(String applyName) {
        this.applyName = applyName;
    }

    public String getApplyPhone() {
        return applyPhone;
    }

    public void setApplyPhone(String applyPhone) {
        this.applyPhone = applyPhone;
    }

    public Integer getApplyGender() {
        return applyGender;
    }

    public void setApplyGender(Integer applyGender) {
        this.applyGender = applyGender;
    }

    public byte[] getLicense() {
        return license;
    }

    public void setLicense(byte[] license) {
        this.license = license;
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

    public Integer getResults() {
        return results;
    }

    public void setResults(Integer results) {
        this.results = results;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }
}
