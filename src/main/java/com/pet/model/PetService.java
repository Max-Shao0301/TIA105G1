package com.pet.model;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enums.Status;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.pet.model.dto.AddPetDTO;
import com.pet.model.dto.PetDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service("petService")
public class PetService {

	@Autowired
	private PetRepository petRepository;
	
	@Autowired
	private MemberService memberService;

	// 查詢該會員的所有寵物(狀態為啟用的)
	public List<PetDTO> findByMemId(Integer memId) {
		List<PetVO> petVOList = petRepository.findByMember_MemId(memId);
		
		//Stream API操作 java8+適用 簡化for迴圈操作
		List<PetDTO> petDTOList = petVOList.stream()
				.map(pet -> new PetDTO(
						pet.getPetId(),
						pet.getPetName(),
						pet.getType(),
						pet.getPetGender(),
						pet.getWeight()
					))
					.collect(Collectors.toList());
		return petDTOList;
	}
	
	//從session直接拿memId  跟上面的方法一樣，只是變會員資料用的
	public List<PetDTO> findByMemId(HttpSession session) {
		Integer memId = (Integer) session.getAttribute("memId");
		
		List<PetVO> petVOList = petRepository.findByMember_MemId(memId);
		
		//Stream API操作 java8+適用 簡化for迴圈操作
		List<PetDTO> petDTOList = petVOList.stream()
				.map(pet -> new PetDTO(
						pet.getPetId(),
						pet.getPetName(),
						pet.getType(),
						pet.getPetGender(),
						pet.getWeight()
					))
					.collect(Collectors.toList());
		return petDTOList;
	}
	
	// 查詢該會員的寵物，第二個參數為狀態，跟上面那個 我不確定哪個方法使用起來比較好
	public List<PetVO> findByMemIdAndStatus(Integer memId, Integer status) {
		return petRepository.findByMember_MemIdAndStatus(memId, status);
	}

	// 根據寵物ID將狀態改為停用
	public void disableByPetId(Integer petId) {
		petRepository.disableByPetId(petId);
	}

	public void addPet(PetVO petVO) {
		petRepository.save(petVO);
	}

	public void updatePet(PetVO petVO) {

		if (petRepository.existsById(petVO.getPetId())) {
			petRepository.save(petVO);
		} else {
			// 此id的物件不存在 不會執行update也不會save
		}
	}

	public PetVO findById(Integer petId) {
		PetVO pet = petRepository.findById(petId).orElse(null);
		return pet;
	}
	
	@Transactional 
	public Integer addPet(AddPetDTO addPetDTO) {
		PetVO petVO = new PetVO();

		MemberVO memberVO = memberService.getOneMember(addPetDTO.getMemId());
		if (memberVO != null) {
			petVO.setMember(memberVO);
			petVO.setPetName(addPetDTO.getPetName());
			petVO.setType(addPetDTO.getType());
			petVO.setPetGender(addPetDTO.getPetGender());
			petVO.setWeight(addPetDTO.getWeight());
			petVO = petRepository.save(petVO);
		}
		return petVO.getPetId();
	}
	
	@Transactional 
	public Integer addPet(AddPetDTO addPetDTO, HttpSession session) {
		PetVO petVO = new PetVO();
		Integer memId = (Integer) session.getAttribute("memId");
		MemberVO memberVO = memberService.getOneMember(memId);
		if (memberVO != null) {
			petVO.setMember(memberVO);
			petVO.setPetName(addPetDTO.getPetName());
			petVO.setType(addPetDTO.getType());
			petVO.setPetGender(addPetDTO.getPetGender());
			petVO.setWeight(addPetDTO.getWeight());
			petVO = petRepository.save(petVO);
		}
		return petVO.getPetId();
	}
	
	@Transactional 
	public void updatePet(PetDTO petDTO, HttpSession session) {
		Integer memId = (Integer) session.getAttribute("memId");
	    MemberVO memberVO = memberService.getOneMember(memId);
	    if (memberVO != null) {
	        PetVO petVO = new PetVO();
	        petVO.setMember(memberVO);
	        petVO.setPetId(petDTO.getPetId());
	        petVO.setPetName(petDTO.getPetName());
	        petVO.setType(petDTO.getType());
	        petVO.setPetGender(petDTO.getPetGender());
	        petVO.setWeight(petDTO.getWeight());
	        petVO.setStatus(Status.open.getNumber());

	        if (petRepository.existsById(petVO.getPetId())) {
	            petRepository.save(petVO);
	        }
	    }
	}

}
