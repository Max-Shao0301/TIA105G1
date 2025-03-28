package com.pet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pet.model.dto.PetDTO;

@Service("petService")
public class PetService {

	@Autowired
	private PetRepository petRepository;

	// 查詢該會員的所有寵物(狀態為啟用的)
	public List<PetDTO> findByMemId(Integer memId) {
		List<PetVO> petVOList = petRepository.findByMember_MemId(memId);
			//Stream API操作 java8+適用 簡化for迴圈操作
		return petVOList.stream()
				.map(pet -> new PetDTO(
					pet.getPetId(),
					pet.getPetName(),
					pet.getType(),
					pet.getPetGender(),
					pet.getWeight()
				))
				.collect(Collectors.toList());
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

}
