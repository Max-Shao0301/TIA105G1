package com.member.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
	@NotBlank(message = "請填入信箱")
	@Email(message = "請確認信箱格式")
	private String loginEmail;

	@NotBlank(message = "請填入密碼")
	private String 	loginPassword;

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public String getLoginEmail() {
		return loginEmail;
	}

	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}
}
