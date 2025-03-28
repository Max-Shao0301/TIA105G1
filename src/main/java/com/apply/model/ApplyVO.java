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
    @NotBlank(message = "請填寫電子郵件")
    @Email(message = "請輸入有效的電子郵件地址")
    private String applyEmail;

    @Column(name="apply_name")
    @NotBlank(message = "請填寫名字")
    private String applyName;

    @Column(name="apply_phone")
    @NotBlank(message = "請填寫電話")
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼")
    private String applyPhone;

    @Column(name="apply_gender")
    @NotNull(message = "請選擇性別")
    private Integer applyGender;

    @Column(name="license")
    @NotNull(message = "請上傳駕照")
    private byte[] license;

    @Column(name="car_number")
    @NotBlank(message = "請填寫車牌號碼")
    private String carNumber;

    @Column(name="introduction")
    @NotBlank(message = "請填寫自我介紹")
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
