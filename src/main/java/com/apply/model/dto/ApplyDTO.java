package com.apply.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ApplyDTO {

    @NotBlank(message = "請填寫名字")
    private String applyName;

    @NotBlank(message = "請填寫電話")
    @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼")
    private String applyPhone;

    @NotBlank(message = "請填寫電子郵件")
    @Email(message = "請輸入有效的電子郵件地址")
    private String applyEmail;

    @NotNull(message = "請選擇性別")
    private Integer applyGender;

    @NotNull(message = "請上傳駕照")
    private byte[] license;

    @NotBlank(message = "請填寫車牌號碼")
    private String carNumber;

    @NotBlank(message = "請填寫自我介紹")
    private String introduction;

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

    public String getApplyEmail() {
        return applyEmail;
    }

    public void setApplyEmail(String applyEmail) {
        this.applyEmail = applyEmail;
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
}
