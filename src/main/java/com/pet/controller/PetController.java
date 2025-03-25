package com.pet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pet.model.PetService;
import com.pet.model.PetVO;

@RestController
public class PetController {

	@Autowired
	private PetService petService;

	//測試功能用 測試push
	
	@GetMapping("/petget/memId")
	public String getPetBymemId() {
		System.out.println(petService.findByMemId(1));
		return "testGet";
	}
	
	@GetMapping("/petget")
	public String getPetById() {
		System.out.println(petService.findById(14));
		return "testGet";
	}

	@PostMapping("/petpost")
	public String postPet(@RequestBody PetVO petVO) {
		petService.addPet(petVO);
		return "testPost";
	}

	@PutMapping("/petput")
	public String putPet(@RequestBody PetVO petVO) {
		petVO.setStatus(1);
		petService.updatePet(petVO);
		return "testPut";
	}

	@PutMapping("/petput/disable")
	public String disablePet(@RequestBody PetVO petVO) {
		Integer petId = petVO.getPetId();
		petService.disableByPetId(petId);
		return "testPut";
	}
}