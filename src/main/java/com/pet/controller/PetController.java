package com.pet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import jakarta.servlet.http.HttpSession;

@Controller
public class PetController {

	@Autowired
	private PetService petService;

	@Autowired
	private MemberService memberService;
	
	//==預約流程相關==//
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

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	//更新寵物
	@PutMapping("/appointment/putPet")
	public ResponseEntity<Map<String, Object>> putPet(@RequestBody PetDTO petDTO, HttpSession session) {

		petService.updatePet(petDTO, session);
		Map<String, Object> response = new HashMap<>();
		response.put("result", "成功更新");

		return ResponseEntity.ok(response);
	}
	
	
	//==會員資料相關==//
	@GetMapping("/member/getMemberPet")
	public ResponseEntity<List<PetDTO>> getPetByMemId(HttpSession session) {

		List<PetDTO> petDTOList = petService.findByMemId(session);

		return ResponseEntity.ok(petDTOList);
	}
	@PostMapping("/member/disablePet")
	public String disablePet(PetDTO petDTO) {
		petService.disableByPetId(petDTO.getPetId());
		 return "redirect:/member"; 
	}
	//新增寵物
	@PostMapping("/member/postPet")
	public ResponseEntity<Map<String, Object>> memberPostPet(@RequestBody AddPetDTO addPetDTO, HttpSession session) {
		
		Integer petId = petService.addPet(addPetDTO, session);
		Map<String, Object> response = new HashMap<>();
		response.put("result", "成功新增");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	//更新寵物
	@PutMapping("/member/putPet")
	public ResponseEntity<Map<String, Object>> memberPutPet(@RequestBody PetDTO petDTO, HttpSession session) {

		petService.updatePet(petDTO, session);
		Map<String, Object> response = new HashMap<>();
		response.put("result", "成功更新");

		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/member/addpet")
	public String addPet() {
		return "/front-end/addpet";
	}
	
	@GetMapping("/member/updatepet")
	public String updatepet() {
		return "/front-end/updatepet";
	}
}