package com.pet.model.dto;

public class AddPetDTO {
	
	private Integer petId;
	private Integer memId;
	private String petName;  
    private String type;    
    private Integer petGender;  
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
