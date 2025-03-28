package com.pet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.MemberService;
import com.pet.model.PetService;
import com.pet.model.PetVO;
import com.pet.model.dto.AddPetDTO;
import com.pet.model.dto.PetDTO;

@Controller
public class PetController {

	@Autowired
	private PetService petService;

	@Autowired
	private MemberService memberService;
	
	//取得該會員的寵物
	@GetMapping("/appointment/getMemberPet")
	public ResponseEntity<List<PetDTO>> getPetByMemId(@RequestParam Integer memId) {

		List<PetDTO> petDTOList = petService.findByMemId(memId);

		return ResponseEntity.ok(petDTOList);
	}

	//新增寵物
	@PostMapping("/appointment/postPet")
	public ResponseEntity<Map<String, Object>> postPet(@RequestBody AddPetDTO addPetDTO) {
		
		Integer petId = petService.addPet(addPetDTO);
		Map<String, Object> response = new HashMap<>();
		response.put("result", "成功新增");
		response.put("petId", petId);

		return ResponseEntity.ok(response);
	}
	
	//更新寵物
	@PutMapping("/appointment/putPet")
	public ResponseEntity<Map<String, Object>> putPet(@RequestBody PetDTO petDTO) {

		petService.updatePet(petDTO);
		Map<String, Object> response = new HashMap<>();
		response.put("result", "成功更新");

		return ResponseEntity.ok(response);
	}
}