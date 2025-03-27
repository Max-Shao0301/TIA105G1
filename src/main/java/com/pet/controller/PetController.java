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

import com.member.model.MemberService;
import com.member.model.MemberVO;
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

	//測試功能用 測試push
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	@GetMapping("/appointment/getMemberPet")
	public ResponseEntity<List<PetDTO>> getPetByMemId(HttpSession session) {
		System.out.println("Session ID: " + session.getId());
	
		Integer memId =(Integer) session.getAttribute("memId");
		
		System.out.println(memId);
		List<PetDTO> petDTOList = petService.findByMemId(1);
		
		return ResponseEntity.ok(petDTOList);
	}
	
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	@PostMapping("/appointment/postPet")
	public  ResponseEntity<Map<String, Object>> postPet(@RequestBody AddPetDTO addPetDTO,HttpSession session) {
		Integer memId =(Integer) session.getAttribute("memId");
		MemberVO memberVO = memberService.getOneMember(memId);
		PetVO petVO = new PetVO();
		Map<String, Object> response = new HashMap<>();
		String result ="";
		if(memberVO != null ) {
			petVO.setMember(memberVO);
			petVO.setPetName(addPetDTO.getPetName());
			petVO.setType(addPetDTO.getType());
			petVO.setPetGender(addPetDTO.getPetGender());
			petVO.setWeight(addPetDTO.getWeight());
			petService.addPet(petVO);
			
			result ="成功更新";
			response.put("result", result);
			return ResponseEntity.ok(response);
		}
		result ="error";
		response.put("result", result);
		return ResponseEntity.badRequest().body(response);
	}
	
	@CrossOrigin(origins = "http://127.0.0.1:5500")
	@PutMapping("/appointment/putPet")
	public  ResponseEntity<Map<String, Object>> putPet(@RequestBody PetDTO petDTO,HttpSession session) {
//		System.out.println(petDTO);
		Integer memId =(Integer) session.getAttribute("memId");
		MemberVO memberVO = memberService.getOneMember(memId);
		Map<String, Object> response = new HashMap<>();
		
		PetVO petVO = new PetVO();
		petVO.setMember(memberVO);
		petVO.setPetId(petDTO.getPetId());
		petVO.setPetName(petDTO.getPetName());
		petVO.setType(petDTO.getType());
		petVO.setPetGender(petDTO.getPetGender());
		petVO.setWeight(petDTO.getWeight());
		petVO.setStatus(1);
		petService.updatePet(petVO);
		
		String result ="成功更新";
		response.put("result", result);
		System.out.println(response.get("result"));
		return ResponseEntity.ok(response);
	}

	@PutMapping("/petput/disable")
	public String disablePet(@RequestBody PetVO petVO) {
		Integer petId = petVO.getPetId();
		petService.disableByPetId(petId);
		return "testPut";
	}
}