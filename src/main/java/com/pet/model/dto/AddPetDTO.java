package com.pet.model.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class AddPetDTO {
	
	@Positive
	private Integer petId;
	
	@Positive
	private Integer memId;
	
	@NotNull(message = "name不得為空")
	private String petName; 
	
	@NotNull(message = "type不得為空")
	@Pattern(regexp = "dog|cat", message = "type必須是dog或cat")
    private String type;    
	
	@Range(min = 0, max = 1 , message = "gender必須是0或1")
    private Integer petGender;  
	
	@Positive(message = "weight必須是正數")
    private Integer weight;
    
	public Integer getPetId() {
		return petId;
	}
	public void setPetId(Integer petId) {
		this.petId = petId;
	}
	public Integer getMemId() {
		return memId;
	}
	public void setMemId(Integer memId) {
		this.memId = memId;
	}
	public String getPetName() {
		return petName;
	}
	public void setPetName(String petName) {
		this.petName = petName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
}
