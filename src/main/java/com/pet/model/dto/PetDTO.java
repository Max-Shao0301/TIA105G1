package com.pet.model.dto;

public class PetDTO {
	
	private Integer petId;
	private String petName;  
    private String type;    
    private Integer petGender;  
    private Integer weight;
    
	public PetDTO(Integer petId, String petName, String type, Integer petGender, Integer weight) {
		super();
		this.petId = petId;
		this.petName = petName;
		this.type = type;
		this.petGender = petGender;
		this.weight = weight;
	}
	public PetDTO() {
		
	}
	public Integer getPetId() {
		return petId;
	}
	public void setPetId(Integer petId) {
		this.petId = petId;
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
